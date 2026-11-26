package com.udistrital.reciclajeppc.data.remote

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/** Cliente minimo (sin librerias extra) para hablar con los nodos Ktor del backend. */
data class NodePost(
    val id: String,
    val authorId: String,
    val originNode: String,
    val text: String,
    val createdAt: Long
)

object NodeApi {

    private fun request(method: String, url: String, body: String? = null): Pair<Int, String> {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 3000
            readTimeout = 5000
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                outputStream.use { it.write(body.toByteArray()) }
            }
        }
        return try {
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            code to (stream?.bufferedReader()?.use { it.readText() } ?: "")
        } finally {
            conn.disconnect()
        }
    }

    suspend fun raw(host: String, port: Int, path: String): String = withContext(Dispatchers.IO) {
        runCatching { request("GET", "http://$host:$port$path").second }
            .getOrElse { "SIN RESPUESTA (${it.javaClass.simpleName})" }
    }

    suspend fun health(host: String, port: Int): String = withContext(Dispatchers.IO) {
        runCatching { request("GET", "http://$host:$port/health").second }
            .getOrElse { "SIN RESPUESTA (${it.javaClass.simpleName})" }
    }

    suspend fun publish(host: String, port: Int, authorId: String, text: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = JSONObject().put("authorId", authorId).put("text", text).toString()
                val (code, resp) = request("POST", "http://$host:$port/posts", body)
                if (code !in 200..299) error("HTTP $code: $resp")
                resp
            }
        }

    suspend fun feed(host: String, port: Int, userId: String): Result<List<NodePost>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val (code, resp) = request("GET", "http://$host:$port/feed/$userId")
                if (code !in 200..299) error("HTTP $code: $resp")
                val arr = JSONArray(resp)
                (0 until arr.length()).map {
                    val o = arr.getJSONObject(it)
                    NodePost(
                        id = o.getString("id"),
                        authorId = o.getString("authorId"),
                        originNode = o.getString("originNode"),
                        text = o.getString("text"),
                        createdAt = o.getLong("createdAt")
                    )
                }
            }
        }
}
