package com.aiope2.feature.remote.tools

import com.aiope2.core.model.RemoteToolBridge
import com.aiope2.feature.remote.core.RemoteExecutionGateway
import com.aiope2.feature.remote.db.RemoteServerDao
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteToolProvider @Inject constructor(
    private val gateway: RemoteExecutionGateway,
    private val serverDao: RemoteServerDao,
) : RemoteToolBridge {

    override fun buildToolDefs(): List<RemoteToolBridge.ToolDef> = listOf(

        RemoteToolBridge.ToolDef(
            name = "ssh_start",
            description = "Open SSH session to server",
            parameters = """{"type":"object","properties":{"server":{"type":"string"}},"required":["server"]}"""
        ),

        RemoteToolBridge.ToolDef(
            name = "ssh_exec",
            description = "Execute command on server",
            parameters = """{"type":"object","properties":{"server":{"type":"string"},"command":{"type":"string"},"timeout":{"type":"integer"}},"required":["server","command"]}""",
            parallel = true
        ),

        RemoteToolBridge.ToolDef(
            name = "ssh_exec_many",
            description = "Execute command across servers",
            parameters = """{"type":"object","properties":{"servers":{"type":"array","items":{"type":"string"}},"command":{"type":"string"}},"required":["servers","command"]}""",
            parallel = true
        ),

        RemoteToolBridge.ToolDef(
            name = "ssh_exit",
            description = "Close SSH session",
            parameters = """{"type":"object","properties":{"server":{"type":"string"}},"required":["server"]}"""
        )
    )

    override suspend fun execute(name: String, args: Map<String, Any?>): String = try {

        when (name) {

            "ssh_exec" -> {
                val server = args["server"].toString()
                val command = args["command"].toString()
                val timeout = (args["timeout"] as? Number)?.toInt() ?: 30

                val serverEntity = serverDao.getByName(server)
                    ?: serverDao.getById(server)
                    ?: return error("Unknown server")

                val result = gateway.exec(serverEntity.id, command, timeout)

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

                val entities = servers.mapNotNull {
                    serverDao.getByName(it) ?: serverDao.getById(it)
                }

                val results = gateway.execMany(
                    entities.map { it.id },
                    command
                )

                JSONObject(results).toString()
            }

            else -> """{"error":"Unsupported tool"}"""
        }

    } catch (e: Exception) {
        JSONObject().put("error", e.message ?: "unknown").toString()
    }

    private fun error(msg: String) =
        """{"error":"$msg"}"""
}