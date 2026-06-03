package com.aiope.daemon.core

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.measureTimeMillis

object CommandExecutor {

    fun exec(command: String, timeout: Int): ExecResult {

        val process = ProcessBuilder("bash", "-c", command)
            .redirectErrorStream(false)
            .start()

        val stdout = StringBuilder()
        val stderr = StringBuilder()

        val time = measureTimeMillis {

            val outThread = Thread {
                BufferedReader(InputStreamReader(process.inputStream)).useLines {
                    it.forEach { line -> stdout.appendLine(line) }
                }
            }

            val errThread = Thread {
                BufferedReader(InputStreamReader(process.errorStream)).useLines {
                    it.forEach { line -> stderr.appendLine(line) }
                }
            }

            outThread.start()
            errThread.start()

            process.waitFor()
            outThread.join()
            errThread.join()
        }

        return ExecResult(
            stdout = stdout.toString().trim(),
            stderr = stderr.toString().trim(),
            exitCode = process.exitValue(),
            durationMs = time
        )
    }
}