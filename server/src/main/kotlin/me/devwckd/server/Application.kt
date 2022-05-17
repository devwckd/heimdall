package me.devwckd.server

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.encodeToConfig
import me.devwckd.server.app.plugins.configureRouting
import me.devwckd.server.app.plugins.configureSerialization
import me.devwckd.server.app.plugins.configureSockets
import me.devwckd.server.config.DEFAULT_CONFIG
import me.devwckd.server.config.HeimdallConfig
import me.devwckd.server.container_driver.ContainerDriver
import me.devwckd.server.container_driver.containerDriverModule
import me.devwckd.server.deployment.Deployment
import me.devwckd.server.deployment.DeploymentManager
import me.devwckd.server.deployment.deploymentModule
import me.devwckd.server.room.roomModule
import org.apache.commons.lang3.RandomStringUtils
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.seconds

suspend fun main() {
    val logger = LoggerFactory.getLogger("heimdall")

    startKoin {
        modules(
            module {
                single { logger }
                single { generateConfig() }
                single { connectToDockerClient() }
            },
            containerDriverModule,
            deploymentModule,
            roomModule
        )
    }

    val scope = CoroutineScope(Executors.newSingleThreadScheduledExecutor().asCoroutineDispatcher())
    scope.launch { purge(logger) }

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSockets()
        configureSerialization()
    }.start(wait = true)
}

fun connectToDockerClient(): DockerClient {
    val dockerClientConfig = DefaultDockerClientConfig
        .createDefaultConfigBuilder()
        .build()

    val dockerHttpClient = ApacheDockerHttpClient.Builder()
        .dockerHost(URI("tcp://localhost:2375/"))
        .maxConnections(100)
        .build()

    return DockerClientImpl.getInstance(dockerClientConfig, dockerHttpClient)
}

@OptIn(ExperimentalSerializationApi::class)
fun generateConfig(): HeimdallConfig {
    val hocon = Hocon {
        useConfigNamingConvention = false
    }

    val configFile = File("./heimdall.conf")
    val config: HeimdallConfig
    if (!configFile.exists()) {
        config = DEFAULT_CONFIG
        configFile.createNewFile()

        val renderOptions =
            ConfigRenderOptions.defaults().setJson(false).setFormatted(true).setOriginComments(false).setComments(false)
        configFile.writeText(hocon.encodeToConfig(config).root().render(renderOptions))
    } else {
        config = hocon.decodeFromConfig(HeimdallConfig.serializer(), ConfigFactory.parseFile(configFile))
    }

    return config
}

suspend fun purge(logger: Logger) {
    delay(5.seconds)

    val containerDriver: ContainerDriver by GlobalContext.get().inject()
    val deploymentManager: DeploymentManager by GlobalContext.get().inject()
    val config: HeimdallConfig by GlobalContext.get().inject()

    logger.info("Purging unsynced containers...")
    val purgeContainers = containerDriver.purgeContainers(deploymentManager.getAll().map(Deployment::id))
    logger.info("Purging complete! ${purgeContainers.affected} containers were removed.")
    logger.info("Creating minimal deployments.")

    config.minDeployments.forEach { (type, amount) ->
        for (i in 0..amount) {
            containerDriver.createContainer(RandomStringUtils.random(8, "abcdefghijklmnopqrstuvwxyz0123456789"), config.types.first { it.name == type }.image)
        }
    }
    logger.info("Minimal deployments created.")
    println(deploymentManager.getAll())
}