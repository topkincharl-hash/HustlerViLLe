package com.aiope2.feature.remote.tools

import com.aiope2.core.model.RemoteToolBridge
import com.aiope2.feature.remote.core.RemoteCapabilityRegistry
import com.aiope2.feature.remote.core.RemoteExecutionRouter
import com.aiope2.feature.remote.core.ToolCapabilityFilter
import com.aiope2.feature.remote.db.RemoteServerDao
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteToolProvider @Inject constructor(
    private val router: RemoteExecutionRouter,
    private val serverDao: RemoteServerDao,
    private val capabilityRegistry: RemoteCapabilityRegistry,
    private val filter: ToolCapabilityFilter
) : RemoteToolBridge {

    override fun buildToolDefs(): List<RemoteToolBridge.ToolDef> = listOf(

        RemoteToolBridge.ToolDef(
            name = "ssh_exec",
            description = "Execute command on server",
            parameters = """{"type":"object","properties":{"server":{"type":"string"},"command":{"type":"string"},"timeout":{"type":"integer"}},"required":["server","command"]}"""
        ),

        RemoteToolBridge.ToolDef(
            name = "ssh_exec_many",
            description = "Multi-server execution",
            parameters = """{"type":"object","properties":{"servers":{"type":"array","items":{"type":"string"}},"command":{"type":"string"}},"required":["servers","command"]}""",
            parallel = true
        )
    )

    override suspend fun execute(name: String, args: Map<String, Any?>): String {

        return try {

            when (name) {

                "ssh_exec" -> {
                    val serverId = args["server"].toString()
                    val command = args["command"].toString()
                    val timeout = (args["timeout"] as? Number)?.toInt() ?: 30

                    val result = router.exec(serverId, command, timeout)

                    JSONObject()
                        .put("stdout", result.stdout)
                        .put("stderr", result.stderr)
                        .put("exit_code", result.exitCode)
                        .put("duration_ms", result.durationMs)
                        .toString()
                }

                "ssh_exec_many" -> {
                    val servers = (args["servers"] as List<*>).map { it.toString() }
                    val command = args["command"].toString()

                    val result = router.execMany(servers, command)

                    JSONObject(result).toString()
                }

                else -> """{"error":"Unknown tool"}"""
            }

        } catch (e: Exception) {
            JSONObject().put("error", e.message ?: "unknown").toString()
        }
    }
}