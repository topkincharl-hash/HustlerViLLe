package com.aiope.daemon.protocol

import kotlinx.serialization.Serializable

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
    val durationMs: Long
)