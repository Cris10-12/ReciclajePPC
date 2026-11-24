package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(val node: String, val db: String)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Nodo ${NodeConfig.nodeId} activo")
        }
        get("/health") {
            val ok = Db.ping()
            val body = HealthResponse(NodeConfig.nodeId, if (ok) "ok" else "down")
            call.respond(if (ok) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable, body)
        }
    }
}