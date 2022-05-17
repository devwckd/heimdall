package me.devwckd.client.deployment

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.devwckd.shared.deployment.DeploymentHeartbeat
import me.devwckd.shared.room.RoomHeartbeat

class RoomClient(
    private val baseUrl: String,
    private val client: HttpClient
) {

    suspend fun heartbeat(request: RoomHeartbeat) {
        val status = client.post {
            url("$baseUrl/room/health/heartbeat")
            setBody(request)
        }.status

        if(status != HttpStatusCode.OK) error("sexo")
    }

}