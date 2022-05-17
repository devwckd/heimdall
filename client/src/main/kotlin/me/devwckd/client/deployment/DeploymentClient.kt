package me.devwckd.client.deployment

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.devwckd.shared.deployment.DeploymentHeartbeat

class DeploymentClient(
    private val baseUrl: String,
    private val client: HttpClient
) {

    suspend fun heartbeat(request: DeploymentHeartbeat) {
        val status = client.post {
            url("$baseUrl/deployment/health/heartbeat")
            setBody(request)
            contentType(ContentType.Application.Json)
            accept(ContentType.Any)
        }.status

        if(status != HttpStatusCode.OK) error("sexo")
    }

}