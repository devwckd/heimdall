package me.devwckd.server.app.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import me.devwckd.server.deployment.deploymentRoutes
import me.devwckd.server.room.roomRoutes

fun Application.configureRouting() {
    routing {
        deploymentRoutes()
        roomRoutes()
    }
}
