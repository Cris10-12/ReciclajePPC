package com.example

import kotlinx.serialization.Serializable

// ---------- Entradas desde la app (requests) ----------

@Serializable
data class CreatePostRequest(
    val authorId: String,
    val text: String
)

@Serializable
data class ConnectionRequest(
    val requesterId: String,
    val receiverId: String
)

@Serializable
data class RespondRequest(
    val connectionId: String,
    val responderId: String,  // debe ser el receiver; el nodo valida la autoridad
    val accept: Boolean
)

// ---------- Entidades del dominio ----------

@Serializable
data class Post(
    val id: String,
    val authorId: String,
    val originNode: String,
    val text: String,
    val createdAt: Long
)

/** Vista de una conexion para la app (coincide con el data class Connection del frontend). */
@Serializable
data class ConnectionView(
    val id: String,
    val requesterId: String,
    val receiverId: String,
    val status: String,
    val createdAt: Long,
    val respondedAt: Long? = null
)

// ---------- Payloads de replicación (nodo -> nodo) ----------

/**
 * Evento que un nodo envía a otro para replicar un cambio.
 * event_id permite la idempotencia: si llega dos veces, se inserta una sola.
 */
@Serializable
data class ReplicationEvent(
    val eventId: String,
    val type: String,          // POST | CONNECTION
    val payload: String,       // JSON del objeto replicado (Post o ConnectionPayload)
    val fromNode: String
)

/**
 * Estado de una conexion, calcado del enum ConnectionStatus de la app:
 * PENDING | ACCEPTED | REJECTED.
 */
@Serializable
data class ConnectionPayload(
    val id: String,
    val pairKey: String,
    val requesterId: String,
    val receiverId: String,
    val status: String,
    val createdAt: Long,
    val respondedAt: Long? = null
)

// ---------- Respuestas genéricas ----------

@Serializable
data class SimpleResponse(
    val ok: Boolean,
    val message: String = "",
    val id: String = ""
)

// ---------- Monitoreo / Auditoría ----------

@Serializable
data class OutboxRow(
    val id: Long,
    val eventId: String,
    val type: String,
    val targetNode: String,
    val status: String,
    val attempts: Int,
    val lastError: String?,
    val createdAt: Long
)

@Serializable
data class ReplicationLogRow(
    val id: Long,
    val ts: Long,
    val node: String,
    val direction: String,
    val eventId: String?,
    val type: String?,
    val peer: String?,
    val result: String?
)

@Serializable
data class MonitorResponse(
    val node: String,
    val peers: Map<String, String>,
    val pendingOutbox: Int,
    val outbox: List<OutboxRow>,
    val replicationLog: List<ReplicationLogRow>
)
