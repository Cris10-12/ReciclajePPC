package com.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.util.UUID

/**
 * Acceso a datos del nodo.
 *
 * Conceptos distribuidos que implementa:
 *  - ACID: crear un post / conexion inserta la entidad + sus filas de outbox en UNA transacción.
 *  - Fragmentación: posts.origin_node y users.home_node.
 *  - Replicación: por cada peer destino se crea una fila PENDING en outbox.
 *  - Idempotencia: inserciones con ON DUPLICATE KEY (+ LWW en connections).
 *  - Autoridad por nodo: solo el hogar del receiver responde una conexión.
 */
object Repository {

    private val json = Json { ignoreUnknownKeys = true }

    // ---------------------------------------------------------------
    // POSTS
    // ---------------------------------------------------------------

    /**
     * Crea un post en el nodo local y encola su replicación hacia todos los peers.
     * Todo ocurre en una transacción ACID: o se guardan post + outbox juntos, o nada.
     */
    fun createPost(authorId: String, text: String): Post {
        val post = Post(
            id = UUID.randomUUID().toString(),
            authorId = authorId,
            originNode = NodeConfig.nodeId,
            text = text,
            createdAt = System.currentTimeMillis()
        )
        Db.inTransaction { conn ->
            insertPost(conn, post)
            val event = ReplicationEvent(
                eventId = post.id,
                type = "POST",
                payload = json.encodeToString(post),
                fromNode = NodeConfig.nodeId
            )
            for (peer in NodeConfig.peers.keys) {
                insertOutbox(conn, event, targetNode = peer)
            }
        }
        return post
    }

    /** Inserta un post de forma idempotente (no duplica si ya existe ese id). */
    fun insertPost(conn: Connection, post: Post) {
        conn.prepareStatement(
            """
            INSERT INTO posts (id, author_id, origin_node, text, created_at)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE id = id
            """.trimIndent()
        ).use { ps ->
            ps.setString(1, post.id)
            ps.setString(2, post.authorId)
            ps.setString(3, post.originNode)
            ps.setString(4, post.text)
            ps.setLong(5, post.createdAt)
            ps.executeUpdate()
        }
    }

    /** Feed de un usuario: sus posts + los posts de sus conexiones ACCEPTED. */
    fun feedForUser(userId: String, limit: Int = 50): List<Post> {
        val sql = """
            SELECT p.id, p.author_id, p.origin_node, p.text, p.created_at
            FROM posts p
            WHERE p.author_id = ?
               OR p.author_id IN (
                   SELECT CASE WHEN c.requester_id = ? THEN c.receiver_id ELSE c.requester_id END
                   FROM connections c
                   WHERE c.status = 'ACCEPTED'
                     AND (c.requester_id = ? OR c.receiver_id = ?)
               )
            ORDER BY p.created_at DESC
            LIMIT ?
        """.trimIndent()
        return Db.withConnection { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, userId)
                ps.setString(2, userId)
                ps.setString(3, userId)
                ps.setString(4, userId)
                ps.setInt(5, limit)
                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(
                                Post(
                                    id = rs.getString("id"),
                                    authorId = rs.getString("author_id"),
                                    originNode = rs.getString("origin_node"),
                                    text = rs.getString("text"),
                                    createdAt = rs.getLong("created_at")
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // CONNECTIONS ("Conectar": solicitud + aceptacion, calcado de la app)
    // ---------------------------------------------------------------

    /** pair_key = los dos ids ordenados y unidos con "|". Un registro por pareja. */
    fun pairKey(a: String, b: String): String =
        if (a <= b) "$a|$b" else "$b|$a"

    /** home_node de un usuario (para decidir autoridad). Null si no existe en este nodo. */
    fun homeNodeOf(conn: Connection, userId: String): String? =
        conn.prepareStatement("SELECT home_node FROM users WHERE id = ?").use { ps ->
            ps.setString(1, userId)
            ps.executeQuery().use { rs -> if (rs.next()) rs.getString(1) else null }
        }

    class ConnectionException(message: String) : RuntimeException(message)

    /**
     * Envia una solicitud de conexion. Reglas (iguales al FakeSocialRepository):
     *  - no se puede conectar consigo mismo.
     *  - un solo registro por pareja (pair_key UNIQUE).
     *  - si existia y estaba REJECTED, se reactiva a PENDING (reenvio).
     *  - si existia PENDING o ACCEPTED, es error (ya existe).
     */
    fun sendRequest(requesterId: String, receiverId: String): ConnectionPayload {
        if (requesterId == receiverId) throw ConnectionException("No puedes conectarte contigo mismo")
        val now = System.currentTimeMillis()
        val key = pairKey(requesterId, receiverId)

        return Db.inTransaction { conn ->
            val existing = findConnectionByPair(conn, key)
            val payload: ConnectionPayload = when {
                existing == null -> ConnectionPayload(
                    id = UUID.randomUUID().toString(),
                    pairKey = key,
                    requesterId = requesterId,
                    receiverId = receiverId,
                    status = "PENDING",
                    createdAt = now,
                    respondedAt = null
                )
                existing.status == "REJECTED" -> existing.copy(
                    requesterId = requesterId,
                    receiverId = receiverId,
                    status = "PENDING",
                    createdAt = now,
                    respondedAt = null
                )
                else -> throw ConnectionException("Ya existe una conexion o solicitud entre estos usuarios")
            }
            upsertConnection(conn, payload)
            enqueueConnectionEvent(conn, payload)
            payload
        }
    }

    /**
     * Responde (acepta/rechaza) una solicitud. AUTORIDAD POR NODO:
     * solo el nodo hogar del receiver procesa la respuesta.
     */
    fun respond(connectionId: String, responderId: String, accept: Boolean): ConnectionPayload {
        val now = System.currentTimeMillis()
        return Db.inTransaction { conn ->
            val existing = findConnectionById(conn, connectionId)
                ?: throw ConnectionException("Conexion no encontrada")
            if (existing.status != "PENDING")
                throw ConnectionException("La solicitud ya fue respondida")
            if (responderId != existing.receiverId)
                throw ConnectionException("Solo el receptor puede responder la solicitud")

            val receiverHome = homeNodeOf(conn, existing.receiverId)
            if (receiverHome != null && receiverHome != NodeConfig.nodeId) {
                throw ConnectionException(
                    "Esta solicitud la decide el nodo '$receiverHome' (hogar del receptor), no '${NodeConfig.nodeId}'"
                )
            }

            val updated = existing.copy(
                status = if (accept) "ACCEPTED" else "REJECTED",
                respondedAt = now
            )
            upsertConnection(conn, updated)
            enqueueConnectionEvent(conn, updated)
            updated
        }
    }

    fun findConnectionByPair(conn: Connection, key: String): ConnectionPayload? =
        conn.prepareStatement(
            "SELECT id, pair_key, requester_id, receiver_id, status, created_at, responded_at FROM connections WHERE pair_key = ?"
        ).use { ps ->
            ps.setString(1, key)
            ps.executeQuery().use { rs -> if (rs.next()) readConnection(rs) else null }
        }

    fun findConnectionById(conn: Connection, id: String): ConnectionPayload? =
        conn.prepareStatement(
            "SELECT id, pair_key, requester_id, receiver_id, status, created_at, responded_at FROM connections WHERE id = ?"
        ).use { ps ->
            ps.setString(1, id)
            ps.executeQuery().use { rs -> if (rs.next()) readConnection(rs) else null }
        }

    private fun readConnection(rs: java.sql.ResultSet) = ConnectionPayload(
        id = rs.getString("id"),
        pairKey = rs.getString("pair_key"),
        requesterId = rs.getString("requester_id"),
        receiverId = rs.getString("receiver_id"),
        status = rs.getString("status"),
        createdAt = rs.getLong("created_at"),
        respondedAt = rs.getLong("responded_at").let { if (rs.wasNull()) null else it }
    )

    /**
     * Inserta o actualiza una conexion por pair_key, con Last-Write-Wins:
     * gana el cambio con responded_at/created_at mas reciente.
     */
    fun upsertConnection(conn: Connection, c: ConnectionPayload) {
        val stamp = c.respondedAt ?: c.createdAt
        conn.prepareStatement(
            """
            INSERT INTO connections (id, pair_key, requester_id, receiver_id, status, created_at, responded_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                requester_id = IF(? >= COALESCE(responded_at, created_at), VALUES(requester_id), requester_id),
                receiver_id  = IF(? >= COALESCE(responded_at, created_at), VALUES(receiver_id),  receiver_id),
                status       = IF(? >= COALESCE(responded_at, created_at), VALUES(status),       status),
                created_at   = IF(? >= COALESCE(responded_at, created_at), VALUES(created_at),   created_at),
                responded_at = IF(? >= COALESCE(responded_at, created_at), VALUES(responded_at), responded_at)
            """.trimIndent()
        ).use { ps ->
            ps.setString(1, c.id)
            ps.setString(2, c.pairKey)
            ps.setString(3, c.requesterId)
            ps.setString(4, c.receiverId)
            ps.setString(5, c.status)
            ps.setLong(6, c.createdAt)
            if (c.respondedAt == null) ps.setNull(7, java.sql.Types.BIGINT) else ps.setLong(7, c.respondedAt)
            for (i in 8..12) ps.setLong(i, stamp)
            ps.executeUpdate()
        }
    }

    private fun enqueueConnectionEvent(conn: Connection, payload: ConnectionPayload) {
        val event = ReplicationEvent(
            eventId = UUID.randomUUID().toString(),
            type = "CONNECTION",
            payload = json.encodeToString(payload),
            fromNode = NodeConfig.nodeId
        )
        for (peer in NodeConfig.peers.keys) {
            insertOutbox(conn, event, targetNode = peer)
        }
    }

    /** Conexiones donde participa el usuario (para la app). */
    fun connectionsOf(userId: String): List<ConnectionView> =
        Db.withConnection { conn ->
            conn.prepareStatement(
                """
                SELECT id, requester_id, receiver_id, status, created_at, responded_at
                FROM connections
                WHERE requester_id = ? OR receiver_id = ?
                ORDER BY created_at DESC
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, userId)
                ps.setString(2, userId)
                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(
                                ConnectionView(
                                    id = rs.getString("id"),
                                    requesterId = rs.getString("requester_id"),
                                    receiverId = rs.getString("receiver_id"),
                                    status = rs.getString("status"),
                                    createdAt = rs.getLong("created_at"),
                                    respondedAt = rs.getLong("responded_at").let { if (rs.wasNull()) null else it }
                                )
                            )
                        }
                    }
                }
            }
        }

    // ---------------------------------------------------------------
    // REPLICACIÓN ENTRANTE (desde un peer, vía POST /replicate)
    // ---------------------------------------------------------------

    /** Aplica un evento replicado de otro nodo. Idempotente (PK + LWW). */
    fun applyReplicationEvent(event: ReplicationEvent) {
        Db.inTransaction { conn ->
            when (event.type) {
                "POST" -> {
                    val post = json.decodeFromString<Post>(event.payload)
                    insertPost(conn, post)
                }
                "CONNECTION" -> {
                    val c = json.decodeFromString<ConnectionPayload>(event.payload)
                    upsertConnection(conn, c)
                }
                else -> throw IllegalArgumentException("Tipo de evento desconocido: ${event.type}")
            }
            logReplication(conn, direction = "IN", eventId = event.eventId,
                type = event.type, peer = event.fromNode, result = "OK")
        }
    }

    // ---------------------------------------------------------------
    // OUTBOX
    // ---------------------------------------------------------------

    fun insertOutbox(conn: Connection, event: ReplicationEvent, targetNode: String) {
        conn.prepareStatement(
            """
            INSERT INTO outbox (event_id, type, payload, target_node, status, attempts, created_at)
            VALUES (?, ?, ?, ?, 'PENDING', 0, ?)
            ON DUPLICATE KEY UPDATE id = id
            """.trimIndent()
        ).use { ps ->
            ps.setString(1, event.eventId)
            ps.setString(2, event.type)
            ps.setString(3, event.payload)
            ps.setString(4, targetNode)
            ps.setLong(5, System.currentTimeMillis())
            ps.executeUpdate()
        }
    }

    data class PendingOutbox(
        val id: Long,
        val eventId: String,
        val type: String,
        val payload: String,
        val targetNode: String,
        val attempts: Int
    )

    /** Lee los eventos pendientes de enviar (cola de propagación). */
    fun fetchPending(limit: Int = 50): List<PendingOutbox> =
        Db.withConnection { conn ->
            conn.prepareStatement(
                """
                SELECT id, event_id, type, payload, target_node, attempts
                FROM outbox
                WHERE status = 'PENDING'
                ORDER BY id ASC
                LIMIT ?
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, limit)
                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(
                                PendingOutbox(
                                    id = rs.getLong("id"),
                                    eventId = rs.getString("event_id"),
                                    type = rs.getString("type"),
                                    payload = rs.getString("payload"),
                                    targetNode = rs.getString("target_node"),
                                    attempts = rs.getInt("attempts")
                                )
                            )
                        }
                    }
                }
            }
        }

    fun markSent(id: Long) = Db.withConnection { conn ->
        conn.prepareStatement("UPDATE outbox SET status = 'SENT' WHERE id = ?").use {
            it.setLong(1, id); it.executeUpdate()
        }
    }

    fun markFailed(id: Long, error: String) = Db.withConnection { conn ->
        conn.prepareStatement(
            "UPDATE outbox SET attempts = attempts + 1, last_error = ? WHERE id = ?"
        ).use {
            it.setString(1, error.take(200)); it.setLong(2, id); it.executeUpdate()
        }
    }

    // ---------------------------------------------------------------
    // AUDITORÍA (replication_log)
    // ---------------------------------------------------------------

    fun logReplication(
        conn: Connection,
        direction: String,
        eventId: String?,
        type: String?,
        peer: String?,
        result: String?
    ) {
        conn.prepareStatement(
            """
            INSERT INTO replication_log (ts, node, direction, event_id, type, peer, result)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
        ).use { ps ->
            ps.setLong(1, System.currentTimeMillis())
            ps.setString(2, NodeConfig.nodeId)
            ps.setString(3, direction)
            ps.setString(4, eventId)
            ps.setString(5, type)
            ps.setString(6, peer)
            ps.setString(7, result)
            ps.executeUpdate()
        }
    }

    fun logReplication(direction: String, eventId: String?, type: String?, peer: String?, result: String?) =
        Db.withConnection { conn -> logReplication(conn, direction, eventId, type, peer, result) }

    // ---------------------------------------------------------------
    // MONITOREO
    // ---------------------------------------------------------------

    fun monitor(): MonitorResponse = Db.withConnection { conn ->
        val outbox = conn.prepareStatement(
            """
            SELECT id, event_id, type, target_node, status, attempts, last_error, created_at
            FROM outbox ORDER BY id DESC LIMIT 100
            """.trimIndent()
        ).use { ps ->
            ps.executeQuery().use { rs ->
                buildList {
                    while (rs.next()) {
                        add(
                            OutboxRow(
                                id = rs.getLong("id"),
                                eventId = rs.getString("event_id"),
                                type = rs.getString("type"),
                                targetNode = rs.getString("target_node"),
                                status = rs.getString("status"),
                                attempts = rs.getInt("attempts"),
                                lastError = rs.getString("last_error"),
                                createdAt = rs.getLong("created_at")
                            )
                        )
                    }
                }
            }
        }

        val log = conn.prepareStatement(
            """
            SELECT id, ts, node, direction, event_id, type, peer, result
            FROM replication_log ORDER BY id DESC LIMIT 100
            """.trimIndent()
        ).use { ps ->
            ps.executeQuery().use { rs ->
                buildList {
                    while (rs.next()) {
                        add(
                            ReplicationLogRow(
                                id = rs.getLong("id"),
                                ts = rs.getLong("ts"),
                                node = rs.getString("node"),
                                direction = rs.getString("direction"),
                                eventId = rs.getString("event_id"),
                                type = rs.getString("type"),
                                peer = rs.getString("peer"),
                                result = rs.getString("result")
                            )
                        )
                    }
                }
            }
        }

        val pending = outbox.count { it.status == "PENDING" }
        MonitorResponse(
            node = NodeConfig.nodeId,
            peers = NodeConfig.peers,
            pendingOutbox = pending,
            outbox = outbox,
            replicationLog = log
        )
    }
}
