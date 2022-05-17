package me.devwckd.shared.deployment

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class DeploymentHeartbeat(
    val deploymentId: String,

    val deploymentTps: Double,
    val deploymentMemory: Double,
    val deploymentCpu: Double,

    val sentAt: Instant
)