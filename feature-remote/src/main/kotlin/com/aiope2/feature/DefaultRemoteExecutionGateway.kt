package com.aiope2.feature.remote.core

import com.aiope2.feature.remote.ssh.ExecResult
import com.aiope2.feature.remote.ssh.SshSessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRemoteExecutionGateway @Inject constructor(
    private val ssh: SshSessionManager
) : RemoteExecutionGateway {

    override suspend fun exec(
        serverId: String,
        command: String,
        timeoutSeconds: Int
    ): ExecResult {
        return ssh.exec(serverId, command, timeoutSeconds)
    }

    override suspend fun execMany(
        serverIds: List<String>,
        command: String,
        timeoutSeconds: Int
    ): Map<String, ExecResult> = coroutineScope {

        serverIds.associateWith { serverId ->
            async {
                ssh.exec(serverId, command, timeoutSeconds)
            }
        }.mapValues { it.value.await() }
    }

    override fun isConnected(serverId: String): Boolean {
        return ssh.isConnected(serverId)
    }
}