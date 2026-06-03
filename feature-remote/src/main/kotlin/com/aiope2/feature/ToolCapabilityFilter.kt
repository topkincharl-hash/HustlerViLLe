package com.aiope2.feature.remote.core

import com.aiope2.core.model.RemoteToolBridge
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Filters available tools based on server capability profile.
 */
@Singleton
class ToolCapabilityFilter @Inject constructor() {

    fun filter(
        tools: List<RemoteToolBridge.ToolDef>,
        caps: RemoteCapabilityRegistry.Capabilities
    ): List<RemoteToolBridge.ToolDef> {

        return tools.filter { tool ->

            when (tool.name) {

                // Always allowed core tools
                "ssh_start", "ssh_exec", "ssh_exit" -> true

                // File ops safe over SSH
                "ssh_read_file",
                "ssh_write_file",
                "ssh_append_file",
                "ssh_delete_file" -> caps.ssh

                // Advanced ops require daemon in future
                "ssh_exec_background",
                "ssh_job_status",
                "ssh_job_logs",
                "ssh_job_kill" -> caps.daemon

                "systemd_status",
                "systemd_restart" -> caps.systemd

                "docker_ps",
                "docker_logs",
                "docker_restart" -> caps.docker

                "ssh_stream_start",
                "ssh_stream_read",
                "ssh_stream_stop" -> caps.streaming

                else -> true
            }
        }
    }
}