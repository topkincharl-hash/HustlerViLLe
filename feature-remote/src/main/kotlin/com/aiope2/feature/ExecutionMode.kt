package com.aiope2.feature.remote.core

/**
 * Defines how a server is currently executed against.
 *
 * This is the foundation for hybrid SSH + daemon control.
 */
enum class ExecutionMode {
    SSH,
    DAEMON,
    FALLBACK
}