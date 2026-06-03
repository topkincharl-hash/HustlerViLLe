package com.aiope.daemon.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.aiope.daemon.protocol.*
import com.aiope.daemon.jobs.*
 com.aiope.daemon.core.CommandExecutor

fun Application.configureRouting() {

    routing {

        /**
         * HEALTH CHECK (your existing __aiope_health__ equivalent)
         */
        get("/api/v1/health") {
            call.respond(
                mapOf(
                    "status" to "online",
                    "daemon" to "aiope",
                    "version" to "1.0"
                )
            )
        }

        /**
         * EXECUTION CORE
         */
        post("/api/v1/exec") {

            val req = call.receive<ExecRequest>()

            val result = CommandExecutor.exec(
                command = req.command,
                timeout = req.timeout
            )

            call.respond(
                ExecResponse(
                    stdout = result.stdout,
                    stderr = result.stderr,
                    exitCode = result.exitCode,
                    durationMs = result.durationMs
                )
            )
        }
    }
}