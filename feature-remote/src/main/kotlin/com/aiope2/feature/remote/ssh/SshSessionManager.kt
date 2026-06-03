package com.aiope2.feature.remote.ssh

import com.aiope2.feature.remote.db.RemoteServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.xfer.FileSystemFile
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class ExecResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val durationMs: Long = 0
)

@Singleton
class SshSessionManager @Inject constructor() {

    private val sessions = ConcurrentHashMap<String, SSHClient>()
    private val knownHosts = ConcurrentHashMap<String, String>()

    private fun normalizeKey(raw: String): String {
        var key = raw.trim()
        if (!key.contains("\n")) key = key.replace("\\n", "\n")
        if (!key.endsWith("\n")) key += "\n"
        return key
    }

    private fun loadKey(client: SSHClient, privateKey: String) =
        client.loadKeys(
            java.io.File.createTempFile("sshkey", ".pem")
                .apply { writeText(normalizeKey(privateKey)) }
                .absolutePath,
            null as String?
        )

    private fun addTofuVerifier(client: SSHClient, host: String, port: Int) {
        val key = "$host:$port"

        client.addHostKeyVerifier { hostname, p, publicKey ->
            val fp = net.schmizz.sshj.common.SecurityUtils.getFingerprint(publicKey)
            val stored = knownHosts[key]
            if (stored == null) {
                knownHosts[key] = fp
                true
            } else stored == fp
        }
    }

    suspend fun connect(server: RemoteServerEntity): String = withContext(Dispatchers.IO) {

        sessions[server.id]?.let {
            if (it.isConnected) return@withContext server.id
        }

        val client = SSHClient()
        addTofuVerifier(client, server.host, server.port)

        client.connect(server.host, server.port)

        val key = server.privateKey
            ?: throw IllegalStateException("Missing private key for ${server.name}")

        try {
            client.authPublickey(server.user, loadKey(client, key))
        } catch (e: Exception) {
            client.disconnect()
            throw IllegalStateException("Auth failed: ${e.message}")
        }

        sessions[server.id] = client
        server.id
    }

    suspend fun connectWithKey(
        host: String,
        port: Int,
        user: String,
        privateKey: String
    ): SSHClient = withContext(Dispatchers.IO) {

        val client = SSHClient()
        addTofuVerifier(client, host, port)

        client.connect(host, port)

        try {
            client.authPublickey(user, loadKey(client, privateKey))
        } catch (e: Exception) {
            client.disconnect()
            throw IllegalStateException("Auth failed: ${e.message}")
        }

        client
    }

    suspend fun exec(
        serverId: String,
        command: String,
        timeout: Int = 30
    ): ExecResult = withContext(Dispatchers.IO) {

        val start = System.currentTimeMillis()

        val client = sessions[serverId]
            ?: throw IllegalStateException("No active session: $serverId")

        val session = client.startSession()

        try {
            val cmd = session.exec(command)
            cmd.join(timeout.toLong(), TimeUnit.SECONDS)

            ExecResult(
                stdout = IOUtils.readFully(cmd.inputStream).toString(Charsets.UTF_8).trimEnd(),
                stderr = IOUtils.readFully(cmd.errorStream).toString(Charsets.UTF_8).trimEnd(),
                exitCode = cmd.exitStatus ?: -1,
                durationMs = System.currentTimeMillis() - start
            )
        } finally {
            session.close()
        }
    }

    fun disconnect(serverId: String) {
        sessions.remove(serverId)?.disconnect()
    }

    fun disconnectAll() {
        sessions.keys.forEach { disconnect(it) }
    }

    fun isConnected(serverId: String): Boolean =
        sessions[serverId]?.isConnected == true
}