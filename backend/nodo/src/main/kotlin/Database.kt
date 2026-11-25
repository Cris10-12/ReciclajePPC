package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import javax.sql.DataSource

object NodeConfig {
    val nodeId: String = (System.getenv("NODE_ID") ?: "norte").trim()
    val dbUrl: String = (System.getenv("DB_URL")
        ?: "jdbc:mysql://localhost:3308/social?allowPublicKeyRetrieval=true&useSSL=false").trim()
    val dbUser: String = (System.getenv("DB_USER") ?: "root").trim()
    val dbPass: String = (System.getenv("DB_PASS") ?: "root").trim()

    /**
     * PEERS: lista de los otros nodos separados por coma, con formato nombre=url.
     * Ejemplo: "sur=http://localhost:8083,central=http://localhost:8081"
     * Si no se define, el nodo corre aislado (sin replicar). Útil para pruebas.
     */
    val peers: Map<String, String> = (System.getenv("PEERS") ?: "")
        .split(",")
        .map { it.trim() }
        .filter { it.contains("=") }
        .associate { entry ->
            val idx = entry.indexOf("=")
            entry.substring(0, idx).trim() to entry.substring(idx + 1).trim()
        }

    /** Todos los nodos del sistema = este nodo + sus peers. Útil para decidir destinos de replicación. */
    val allNodes: Set<String> get() = peers.keys + nodeId
}

object Db {
    val dataSource: DataSource by lazy {
        HikariDataSource(HikariConfig().apply {
            jdbcUrl = NodeConfig.dbUrl
            username = NodeConfig.dbUser
            password = NodeConfig.dbPass
            maximumPoolSize = 8
            connectionTimeout = 3000
        })
    }

    fun ping(): Boolean = try {
        dataSource.connection.use { c -> c.createStatement().use { it.execute("SELECT 1") } }
        true
    } catch (e: Exception) {
        false
    }

    /** Ejecuta un bloque con una conexión del pool (auto-cierre). */
    fun <T> withConnection(block: (Connection) -> T): T =
        dataSource.connection.use { block(it) }

    /**
     * Ejecuta un bloque dentro de una transacción ACID: todo o nada.
     * Si el bloque lanza excepción, se hace rollback automático.
     */
    fun <T> inTransaction(block: (Connection) -> T): T =
        dataSource.connection.use { conn ->
            val previousAutoCommit = conn.autoCommit
            conn.autoCommit = false
            try {
                val result = block(conn)
                conn.commit()
                result
            } catch (e: Exception) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = previousAutoCommit
            }
        }
}
