package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val node: String, val db: String)

fun Application.configureRouting() {

    // Arranca el worker de replicación cuando inicia la app y lo detiene al apagar.
    val worker = OutboxWorker()
    worker.start()
    monitor.subscribe(ApplicationStopped) { worker.stop() }

    routing {

        get("/") {
            call.respondText("Nodo ${NodeConfig.nodeId} activo. Peers: ${NodeConfig.peers.keys}")
        }

        get("/health") {
            val ok = Db.ping()
            val body = HealthResponse(NodeConfig.nodeId, if (ok) "ok" else "down")
            call.respond(if (ok) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable, body)
        }

        // ----- POSTS -----

        // Crear un post (ACID: post + outbox en una transaccion). Responde ya, sin esperar peers (AP).
        post("/posts") {
            val req = call.receive<CreatePostRequest>()
            if (req.authorId.isBlank() || req.text.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "authorId y text son obligatorios"))
                return@post
            }
            val created = Repository.createPost(req.authorId, req.text)
            call.respond(HttpStatusCode.Created, created)
        }

        // Feed de un usuario: sus posts + los de sus conexiones ACCEPTED (ya replicados a este nodo).
        get("/feed/{userId}") {
            val userId = call.parameters["userId"].orEmpty()
            if (userId.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "userId requerido"))
                return@get
            }
            call.respond(Repository.feedForUser(userId))
        }

        // ----- CONNECTIONS (Conectar: solicitud + aceptacion) -----

        // Enviar solicitud de conexion.
        post("/connections") {
            val req = call.receive<ConnectionRequest>()
            if (req.requesterId.isBlank() || req.receiverId.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "requesterId y receiverId son obligatorios"))
                return@post
            }
            try {
                val c = Repository.sendRequest(req.requesterId, req.receiverId)
                call.respond(HttpStatusCode.Created, c)
            } catch (e: Repository.ConnectionException) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "conflicto"))
            }
        }

        // Responder (aceptar/rechazar). Solo el nodo hogar del receptor la procesa.
        post("/connections/respond") {
            val req = call.receive<RespondRequest>()
            if (req.connectionId.isBlank() || req.responderId.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "connectionId y responderId son obligatorios"))
                return@post
            }
            try {
                val c = Repository.respond(req.connectionId, req.responderId, req.accept)
                call.respond(c)
            } catch (e: Repository.ConnectionException) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "conflicto"))
            }
        }

        // Conexiones donde participa un usuario.
        get("/connections/{userId}") {
            val userId = call.parameters["userId"].orEmpty()
            if (userId.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "userId requerido"))
                return@get
            }
            call.respond(Repository.connectionsOf(userId))
        }

        // ----- REPLICACION ENTRANTE (nodo -> nodo) -----

        // Recibe un evento de otro nodo y lo aplica de forma idempotente.
        post("/replicate") {
            val event = call.receive<ReplicationEvent>()
            try {
                Repository.applyReplicationEvent(event)
                call.respond(SimpleResponse(true, "aplicado", event.eventId))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    SimpleResponse(false, e.message ?: "error aplicando evento")
                )
            }
        }

        // ----- MONITOREO / AUDITORIA -----

        // Devuelve outbox + replication_log + pendientes. Es el panel para la demo y el PDF.
        get("/monitor") {
            call.respond(Repository.monitor())
        }
    }
}
