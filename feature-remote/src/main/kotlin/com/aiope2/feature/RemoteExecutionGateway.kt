package com.aiope2.feature.remote.core

import com.aiope2.feature.remote.ssh.ExecResult

interface RemoteExecutionGateway {

    suspend fun exec(
        serverId: String,
        command: String,
        timeoutSeconds: Int = 30
    ): ExecResult

    suspend fun execMany(
        serverIds: List<String>,
        command: String,
        timeoutSeconds: Int = 30
    ): Map<String, ExecResult>

    fun isConnected(serverId: String): Boolean
}