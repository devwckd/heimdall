package me.devwckd.server.deployment

import kotlinx.datetime.Instant
import java.util.concurrent.ConcurrentHashMap

class DeploymentManager {
    private val deploymentsById = ConcurrentHashMap<String, Deployment>()

    fun getById(deploymentId: String): Deployment? {
        return deploymentsById[deploymentId]
    }

    fun put(deploymentId: String, deployment: Deployment) {
        deploymentsById[deploymentId] = deployment
    }

    fun getAll() = deploymentsById.values.toList()
}