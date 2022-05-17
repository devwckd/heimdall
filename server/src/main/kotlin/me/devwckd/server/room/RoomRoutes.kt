package me.devwckd.server.room

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import me.devwckd.shared.room.RoomHeartbeat
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Route.roomRoutes() = route("room") {
    val logger = LoggerFactory.getLogger("room")
    val roomManager: RoomManager by inject()

    post("health/heartbeat") {
        val request: RoomHeartbeat = call.receive()
        val room = roomManager.getById(request.deploymentId, request.roomId)
            ?: return@post call.respond(HttpStatusCode.BadRequest)

        room.status.apply {
            state = request.roomState
            currentPlayers = request.currentPlayers
            lastHeartbeat = Clock.System.now()
        }

        logger.info("received heartbeat from room \"${request.roomId}\" on deployment \"${request.deploymentId}\".")

        call.respond(HttpStatusCode.OK)
    }
}