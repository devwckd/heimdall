package me.devwckd.server.config

import kotlinx.serialization.Serializable

val DEFAULT_CONFIG = HeimdallConfig(
    types = listOf(
        DeploymentType(
            name = "test",
            image = "heimdall-test:latest"
        )
    ),
    minDeployments = mapOf(
        "test" to 1
    )
)

@Serializable
data class HeimdallConfig(
    val types: List<DeploymentType>,
    val minDeployments: Map<String, Int>
)

@Serializable
data class DeploymentType(
    val name: String,
    val image: String,
)