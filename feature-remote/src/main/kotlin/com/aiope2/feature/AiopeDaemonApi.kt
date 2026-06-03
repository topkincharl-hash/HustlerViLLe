package com.aiope2.feature.remote.daemon

/**
 * DAEMON CONTRACT SPEC
 *
 * Remote agent must expose:
 *
 * POST /api/v1/exec
 */
object AiopeDaemonApi {

    const val EXEC_ENDPOINT = "/api/v1/exec"

    /**
     * Expected request:
     *
     * {
     *   "command": "ls -la",
     *   "timeout": 30
     * }
     *
     * Response:
     *
     * {
     *   "stdout": "...",
     *   "stderr": "...",
     *   "exitCode": 0,
     *   "durationMs": 12
     * }
     */
}