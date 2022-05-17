package me.devwckd.server.deployment

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import me.devwckd.server.container_driver.ContainerDriver
import me.devwckd.shared.deployment.DeploymentHeartbeat
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Route.deploymentRoutes() = route("deployment") {
    val logger = LoggerFactory.getLogger("deployment")
    val deploymentManager: DeploymentManager by inject()
    val containerDriver: ContainerDriver by inject()

    post("health/heartbeat") {
        val request: DeploymentHeartbeat = call.receive()
        var deployment = deploymentManager.getById(request.deploymentId)

        if (deployment == null) {
            logger.warn("Deployment '${request.deploymentId}' sent a heartbeat but it is unregistered, trying to register it...")
            deployment = containerDriver.getContainerMetadata(request.deploymentId)
                ?.let { containerResult ->
                    Deployment(
                        request.deploymentId,
                        containerResult.deploymentAddress,
                        containerResult.deploymentPort,
                        containerResult.deploymentType,
                        DeploymentHealth(Clock.System.now())
                    ).also { newDeployment ->
                        deploymentManager.put(newDeployment.id, newDeployment)
                    }
                }
        }

        if (deployment == null) {
            logger.warn("Deployment '${request.deploymentId}' sent a heartbeat but it is unregistered and it isn't located in a heimdall container, killing...")

            containerDriver.killContainer(request.deploymentId)
            return@post call.respond(HttpStatusCode.BadRequest)
        }

        deployment.health.apply {
            lastHeartbeat = Clock.System.now()
            pushTps(request.deploymentTps)
            pushMemory(request.deploymentMemory)
            pushCpu(request.deploymentCpu)
            pushPing((Clock.System.now() - request.sentAt).inWholeMilliseconds)
        }

        call.respond(HttpStatusCode.OK)
    }
}
