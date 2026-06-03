package com.aiope.daemon.jobs

import java.io.BufferedReader
import java.io.InputStreamReader

object StreamingExecutor {

    fun runJob(job: Job) {

        try {
            val process = ProcessBuilder("bash", "-c", job.command)
                .redirectErrorStream(false)
                .start()

            val outThread = Thread {
                BufferedReader(InputStreamReader(process.inputStream)).useLines {
                    it.forEach { line ->
                        job.stdout.appendLine(line)
                        JobStore.update(job)
                    }
                }
            }

            val errThread = Thread {
                BufferedReader(InputStreamReader(process.errorStream)).useLines {
                    it.forEach { line ->
                        job.stderr.appendLine(line)
                        JobStore.update(job)
                    }
                }
            }

            outThread.start()
            errThread.start()

            val exit = process.waitFor()

            outThread.join()
            errThread.join()

            job.exitCode = exit
            job.status = if (exit == 0) JobStatus.COMPLETED else JobStatus.FAILED

            JobStore.update(job)

        } catch (e: Exception) {
            job.status = JobStatus.FAILED
            job.stderr.appendLine(e.message ?: "unknown error")
            JobStore.update(job)
        }
    }
}