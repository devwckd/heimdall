package me.devwckd.server.container_driver.impl

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerCmd
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import me.devwckd.server.container_driver.*
import java.net.ServerSocket

data class DockerContainerDriver(
    private val dockerClient: DockerClient
) : ContainerDriver {

    override fun createContainer(deploymentId: String, image: String): CreateContainerResult {
        val port = ServerSocket(0).let { it.close(); it.localPort}

        val hostConfig = HostConfig.newHostConfig()
        hostConfig.withPortBindings(PortBinding(Ports.Binding.bindPort(port), ExposedPort(25565)))
        hostConfig.withExtraHosts("host.docker.internal:host-gateway")

        val cmd = dockerClient.createContainerCmd(image)
            .withHostConfig(hostConfig)
            .withName(deploymentId)
            .withLabels(mutableMapOf(
                "heimdall.id" to deploymentId,
                "heimdall.address" to "localhost",
                "heimdall.port" to port.toString(),
                "heimdall.type" to "type"
            ))

        val response = cmd.exec()
        dockerClient.startContainerCmd(response.id).exec()
        return DockerCreateContainerResult(response.id, port)
    }

    override fun getContainerMetadata(id: String): ContainerMetadataResult? {
        val labels = dockerClient.inspectContainerCmd(id).exec().config.labels ?: return null
        val deploymentAddress = labels["heimdall.address"] ?: return null
        val deploymentPort = labels["heimdall.port"]?.toIntOrNull() ?: return null
        val deploymentType = labels["heimdall.type"] ?: return null

        return DockerContainerMetadataResult(deploymentAddress, deploymentPort, deploymentType)
    }

    override fun killContainer(id: String): KillContainerResult? {
        dockerClient.killContainerCmd(id).exec()
        return DockerKillContainerResult()
    }

    override fun purgeContainers(except: List<String>): PurgeContainersResult {
        var total = 0

        dockerClient.listContainersCmd()
            .withShowAll(true)
            .exec()
            .filter { it.labels["heimdall.id"] != null }
            .filter { !except.contains(it.labels["heimdall.id"]!!) }
            .forEach { container ->
                dockerClient.removeContainerCmd(container.id).withForce(true).exec()
                total++
            }

        return DockerPurgeContainersResult(total)
    }

}

data class DockerCreateContainerResult(
    override val containerId: String,
    override val publishedPort: Int
) : CreateContainerResult

data class DockerContainerMetadataResult(
    override val deploymentAddress: String,
    override val deploymentPort: Int,
    override val deploymentType: String
) : ContainerMetadataResult

class DockerKillContainerResult : KillContainerResult

data class DockerPurgeContainersResult(
    override val affected: Int
) : PurgeContainersResult