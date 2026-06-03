package com.aiope2.feature.remote.daemon

import com.aiope2.feature.remote.db.RemoteServerDao
import com.aiope2.feature.remote.ssh.ExecResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hybrid execution layer:
 * - daemon first (future)
 * - SSH fallback (current reality)
 */
@Singleton
class DaemonExecutionGateway @Inject constructor(
    private val daemon: AiopeDaemonClient,
    private val sshFallback: com.aiope2.feature.remote.core.RemoteExecutionGateway,
    private val dao: RemoteServerDao
) {

    suspend fun exec(
        serverId: String,
        command: String,
        timeout: Int
    ): ExecResult = withContext(Dispatchers.IO) {

        val server = dao.getById(serverId)
            ?: throw IllegalStateException("Unknown server")

        return@withContext try {

            // FUTURE SWITCH POINT
            val daemonEnabled = server.port == 2222

            if (daemonEnabled) {
                val res = daemon.exec(
                    server.host,
                    server.port,
                    AiopeDaemonClient.ExecRequest(command, timeout)
                )

                ExecResult(
                    stdout = res.stdout,
                    stderr = res.stderr,
                    exitCode = res.exitCode,
                    durationMs = res.durationMs
                )
            } else {
                sshFallback.exec(serverId, command, timeout)
            }

        } catch (e: Exception) {
            // HARD FALLBACK TO SSH
            sshFallback.exec(serverId, command, timeout)
        }
    }
}