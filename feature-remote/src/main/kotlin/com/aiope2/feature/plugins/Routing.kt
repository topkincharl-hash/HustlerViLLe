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
    
 post("/api/v1/job/start") {
    val req = call.receive<ExecRequest>()

    val job = JobStore.create(req.command)

    Thread {
        StreamingExecutor.runJob(job)
    }.start()

    call.respond(
        mapOf(
            "jobId" to job.id,
            "status" to job.status
        )
    )
}

get("/api/v1/job/{id}") {
    val id = call.parameters["id"]!!
    val job = JobStore.get(id)

    if (job == null) {
        call.respond(mapOf("error" to "not_found"))
        return@get
    }

    call.respond(
        mapOf(
            "id" to job.id,
            "status" to job.status,
            "stdout" to job.stdout.toString(),
            "stderr" to job.stderr.toString(),
            "exitCode" to job.exitCode
        )
    )
}

post("/api/v1/job/{id}/kill") {
    val id = call.parameters["id"]!!
    val job = JobStore.get(id)

    if (job != null) {
        job.status = JobStatus.KILLED
        JobStore.update(job)
    }

    call.respond(mapOf("status" to "killed"))
}
}

