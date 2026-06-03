package com.aiope2.feature.remote.core

import com.aiope2.feature.remote.db.RemoteServerDao
import com.aiope2.feature.remote.ssh.ExecResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central execution router.
 *
 * This is the abstraction that will eventually allow:
 * - SSH execution (current)
 * - AIOPE daemon execution (future)
 * - Hybrid fallback routing
 */
@Singleton
class RemoteExecutionRouter @Inject constructor(
    private val gateway: RemoteExecutionGateway,
    private val serverDao: RemoteServerDao
) {

    /**
     * Primary execution entry point.
     * All tools SHOULD use this instead of direct SSH.
     */
    suspend fun exec(
        serverId: String,
        command: String,
        timeoutSeconds: Int = 30
    ): ExecResult {

        val server = serverDao.getById(serverId)
            ?: throw IllegalStateException("Unknown server: $serverId")

        // FUTURE EXTENSION POINT:
        // if (server.capabilities contains "daemon") -> use daemon API

        return gateway.exec(serverId, command, timeoutSeconds)
    }

    /**
     * Multi-server execution with controlled fan-out.
     */
    suspend fun execMany(
        serverIds: List<String>,
        command: String,
        timeoutSeconds: Int = 30
    ): Map<String, ExecResult> {

        val validServers = serverIds.mapNotNull { id ->
            serverDao.getById(id)
        }

        return gateway.execMany(
            validServers.map { it.id },
            command,
            timeoutSeconds
        )
    }

    /**
     * Connectivity abstraction (can later include daemon health checks).
     */
    fun isConnected(serverId: String): Boolean {
        return gateway.isConnected(serverId)
    }

    /**
     * Future hook:
     * - detect SSH vs daemon availability
     * - cache routing decision
     */
    suspend fun detectExecutionMode(serverId: String): ExecutionMode {
        val server = serverDao.getById(serverId)
            ?: throw IllegalStateException("Unknown server")

        // CURRENT MODE: SSH ONLY
        return ExecutionMode.SSH
    }
}