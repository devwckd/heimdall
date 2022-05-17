package me.devwckd.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.devwckd.client.deployment.DeploymentClient
import me.devwckd.client.deployment.RoomClient

class HeimdallClient(
    rawBaseUrl: String,
) {

    private val baseUrl = rawBaseUrl.removeSuffix("/")

    private val client by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    val deployment by lazy {
        DeploymentClient(baseUrl, client)
    }

    val room by lazy {
        RoomClient(baseUrl, client)
    }
}