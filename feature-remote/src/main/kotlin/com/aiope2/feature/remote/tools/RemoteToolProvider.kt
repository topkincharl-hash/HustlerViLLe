class RemoteToolProvider @Inject constructor(
private val sshManager: SshSessionManager,
private val serverDao: RemoteServerDao,
) : RemoteToolBridge {

override fun buildToolDefs(): List<RemoteToolBridge.ToolDef> = listOf(

    // ===========================
    // CONNECTION
    // ===========================

    RemoteToolBridge.ToolDef(
        name = "ssh_start",
        description = "Open persistent SSH session to a remote server.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"}
          },
          "required":["server"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_status",
        description = "Get SSH connection state and server information.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"}
          },
          "required":["server"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_exit",
        description = "Close active SSH session.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"}
          },
          "required":["server"]
        }"""
    ),

    // ===========================
    // EXECUTION
    // ===========================

    RemoteToolBridge.ToolDef(
        name = "ssh_exec",
        description = "Execute shell command on remote server.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "command":{"type":"string"},
            "timeout":{"type":"integer"}
          },
          "required":["server","command"]
        }""",
        parallel = true
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_exec_many",
        description = "Execute the same command across multiple servers.",
        parameters = """{
          "type":"object",
          "properties":{
            "servers":{
              "type":"array",
              "items":{"type":"string"}
            },
            "command":{"type":"string"}
          },
          "required":["servers","command"]
        }""",
        parallel = true
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_exec_background",
        description = "Run command in background and return job information.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "command":{"type":"string"}
          },
          "required":["server","command"]
        }"""
    ),

    // ===========================
    // DISCOVERY
    // ===========================

    RemoteToolBridge.ToolDef(
        name = "ssh_discover",
        description = "Discover operating system, uptime, disk, memory, docker and services.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"}
          },
          "required":["server"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_history",
        description = "View command history for server.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"}
          },
          "required":["server"]
        }"""
    ),

    // ===========================
    // FILES
    // ===========================

    RemoteToolBridge.ToolDef(
        name = "ssh_read_file",
        description = "Read remote file.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "path":{"type":"string"}
          },
          "required":["server","path"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_write_file",
        description = "Write content to remote file.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "path":{"type":"string"},
            "content":{"type":"string"}
          },
          "required":["server","path","content"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_append_file",
        description = "Append content to remote file.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "path":{"type":"string"},
            "content":{"type":"string"}
          },
          "required":["server","path","content"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_delete_file",
        description = "Delete remote file.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "path":{"type":"string"}
          },
          "required":["server","path"]
        }"""
    ),

    // ===========================
    // JOBS
    // ===========================

    RemoteToolBridge.ToolDef(
        name = "ssh_job_status",
        description = "Check status of background job.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "jobId":{"type":"string"}
          },
          "required":["server","jobId"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_job_logs",
        description = "Retrieve background job logs.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "jobId":{"type":"string"}
          },
          "required":["server","jobId"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_job_kill",
        description = "Terminate background job.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "jobId":{"type":"string"}
          },
          "required":["server","jobId"]
        }"""
    ),

    // ===========================
    // SYSTEMD
    // ===========================

    RemoteToolBridge.ToolDef(
        name = "systemd_status",
        description = "Get service status.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "service":{"type":"string"}
          },
          "required":["server","service"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "systemd_restart",
        description = "Restart service.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "service":{"type":"string"}
          },
          "required":["server","service"]
        }"""
    ),

    // ===========================
    // DOCKER
    // ===========================

    RemoteToolBridge.ToolDef(
        name = "docker_ps",
        description = "List running containers.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"}
          },
          "required":["server"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "docker_logs",
        description = "Retrieve container logs.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "container":{"type":"string"}
          },
          "required":["server","container"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "docker_restart",
        description = "Restart docker container.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "container":{"type":"string"}
          },
          "required":["server","container"]
        }"""
    ),

    // ===========================
    // STREAMING
    // ===========================

    RemoteToolBridge.ToolDef(
        name = "ssh_stream_start",
        description = "Start streamed command execution.",
        parameters = """{
          "type":"object",
          "properties":{
            "server":{"type":"string"},
            "command":{"type":"string"}
          },
          "required":["server","command"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_stream_read",
        description = "Read stream output.",
        parameters = """{
          "type":"object",
          "properties":{
            "streamId":{"type":"string"}
          },
          "required":["streamId"]
        }"""
    ),

    RemoteToolBridge.ToolDef(
        name = "ssh_stream_stop",
        description = "Stop active stream.",
        parameters = """{
          "type":"object",
          "properties":{
            "streamId":{"type":"string"}
          },
          "required":["streamId"]
        }"""
    )
)

override suspend fun execute(
    name: String,
    args: Map<String, Any?>
): String = try {
    when (name) {

        "ssh_start" -> sshStart(args)
        "ssh_status" -> sshStatus(args)
        "ssh_exit" -> sshExit(args)

        "ssh_exec" -> sshExec(args)
        "ssh_exec_many" -> sshExecMany(args)
        "ssh_exec_background" -> sshExecBackground(args)

        "ssh_discover" -> sshDiscover(args)
        "ssh_history" -> sshHistory(args)

        "ssh_read_file" -> sshReadFile(args)
        "ssh_write_file" -> sshWriteFile(args)
        "ssh_append_file" -> sshAppendFile(args)
        "ssh_delete_file" -> sshDeleteFile(args)

        "ssh_job_status" -> sshJobStatus(args)
        "ssh_job_logs" -> sshJobLogs(args)
        "ssh_job_kill" -> sshJobKill(args)

        "systemd_status" -> systemdStatus(args)
        "systemd_restart" -> systemdRestart(args)

        "docker_ps" -> dockerPs(args)
        "docker_logs" -> dockerLogs(args)
        "docker_restart" -> dockerRestart(args)

        "ssh_stream_start" -> sshStreamStart(args)
        "ssh_stream_read" -> sshStreamRead(args)
        "ssh_stream_stop" -> sshStreamStop(args)

        else -> """{"error":"Unknown tool: $name"}"""
    }
} catch (e: Exception) {
    JSONObject()
        .put("error", e.message ?: "Unknown error")
        .toString()
}

}