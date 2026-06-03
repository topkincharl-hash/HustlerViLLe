package com.aiope.daemon

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.aiope.daemon.plugins.configureRouting
import com.aiope.daemon.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 2222, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}