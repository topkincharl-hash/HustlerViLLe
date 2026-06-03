package com.aiope2.feature.remote.daemon

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/**
 * Minimal HTTP-based daemon protocol client.
 *
 * Replaces SSH as primary execution layer over time.
 */
class AiopeDaemonClient(
    private val json: Json = Json { ignoreUnknownKeys = true }
) {

    @Serializable
    data class ExecRequest(
        val command: String,
        val timeout: Int = 30
    )

    @Serializable
    data class ExecResponse(
        val stdout: String,
        val stderr: String,
        val exitCode: Int,
        val durationMs: Long = 0
    )

    suspend fun exec(
        host: String,
        port: Int,
        request: ExecRequest
    ): ExecResponse = withContext(Dispatchers.IO) {

        val url = URL("http://$host:$port/api/v1/exec")

        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 5000
            readTimeout = request.timeout * 1000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        val body = json.encodeToString(
            ExecRequest.serializer(),
            request
        )

        conn.outputStream.use { it.write(body.toByteArray()) }

        val response = conn.inputStream.bufferedReader().readText()

        json.decodeFromString(
            ExecResponse.serializer(),
            response
        )
    }
}