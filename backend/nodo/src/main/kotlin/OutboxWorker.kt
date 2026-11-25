package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

/**
 * Worker de replicación (patrón Outbox).
 *
 * Cada [intervalMs] milisegundos:
 *   1. Lee eventos PENDING de la tabla outbox.
 *   2. Intenta entregarlos por HTTP al nodo destino (POST /replicate).
 *   3. Si el destino responde OK -> marca SENT y registra en replication_log (OUT, OK).
 *   4. Si el destino está caído (partición) -> incrementa attempts y deja la fila PENDING.
 *      Esto es la evidencia visible del split-brain: la cola crece mientras el peer no vuelve.
 *
 * Al reconectar el peer, el worker reintenta y la cola se vacía sola (consistencia eventual).
 */
class OutboxWorker(
    private val intervalMs: Long = 3000L
) {
    private val log = LoggerFactory.getLogger("OutboxWorker")
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
        install(HttpTimeout) {
            requestTimeoutMillis = 3000
            connectTimeoutMillis = 2000
        }
    }

    fun start() {
        if (NodeConfig.peers.isEmpty()) {
            log.warn("Nodo '{}' sin PEERS configurados: el worker de replicacion no enviara nada.", NodeConfig.nodeId)
        } else {
            log.info("Worker de outbox iniciado en nodo '{}'. Peers: {}", NodeConfig.nodeId, NodeConfig.peers)
        }
        scope.launch {
            while (isActive) {
                runCatching { tick() }.onFailure { log.error("Error en ciclo del worker: {}", it.message) }
                delay(intervalMs)
            }
        }
    }

    private suspend fun tick() {
        val pending = Repository.fetchPending()
        if (pending.isEmpty()) return

        for (item in pending) {
            val peerUrl = NodeConfig.peers[item.targetNode]
            if (peerUrl == null) {
                // No sabemos la URL de ese destino: lo marcamos como error pero no bloqueamos.
                Repository.markFailed(item.id, "peer desconocido: ${item.targetNode}")
                continue
            }

            val event = ReplicationEvent(
                eventId = item.eventId,
                type = item.type,
                payload = item.payload,
                fromNode = NodeConfig.nodeId
            )

            try {
                val resp: HttpResponse = client.post("$peerUrl/replicate") {
                    contentType(ContentType.Application.Json)
                    setBody(event)
                }
                if (resp.status.isSuccess()) {
                    Repository.markSent(item.id)
                    Repository.logReplication(
                        direction = "OUT", eventId = item.eventId, type = item.type,
                        peer = item.targetNode, result = "OK"
                    )
                    log.info("Replicado {} ({}) -> {}", item.type, item.eventId.take(8), item.targetNode)
                } else {
                    Repository.markFailed(item.id, "HTTP ${resp.status.value}")
                    Repository.logReplication(
                        direction = "OUT", eventId = item.eventId, type = item.type,
                        peer = item.targetNode, result = "HTTP_${resp.status.value}"
                    )
                }
            } catch (e: Exception) {
                // Nodo destino caído / partición de red: la fila queda PENDING y se reintenta.
                Repository.markFailed(item.id, e.message ?: "sin conexion")
                Repository.logReplication(
                    direction = "OUT", eventId = item.eventId, type = item.type,
                    peer = item.targetNode, result = "FALLO"
                )
                log.warn("No se pudo replicar hacia {} (queda PENDING): {}", item.targetNode, e.message)
            }
        }
    }

    fun stop() {
        scope.cancel()
        client.close()
    }
}
