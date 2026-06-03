package com.aiope2.feature.remote.core

import com.aiope2.feature.remote.db.RemoteServerDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Determines what each server is capable of.
 *
 * This is the foundation for:
 * - tool filtering
 * - daemon vs SSH routing
 * - safe execution boundaries
 */
@Singleton
class RemoteCapabilityRegistry @Inject constructor(
    private val serverDao: RemoteServerDao
) {

    data class Capabilities(
        val ssh: Boolean = true,
        val daemon: Boolean = false,
        val docker: Boolean = false,
        val systemd: Boolean = false,
        val streaming: Boolean = false
    )

    /**
     * Current version uses DB only.
     * Future: will be enriched by __aiope_health__.
     */
    suspend fun get(serverId: String): Capabilities {
        val server = serverDao.getById(serverId)
            ?: throw IllegalStateException("Unknown server")

        // Minimal heuristic layer for now
        val hasDaemon = server.port == 2222

        return Capabilities(
            ssh = true,
            daemon = hasDaemon,
            docker = false,
            systemd = false,
            streaming = false
        )
    }

    fun supportsDocker(cap: Capabilities) = cap.docker
    fun supportsSystemd(cap: Capabilities) = cap.systemd
    fun supportsDaemon(cap: Capabilities) = cap.daemon
}