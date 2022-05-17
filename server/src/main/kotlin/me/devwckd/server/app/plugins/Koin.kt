package me.devwckd.server.app.plugins

import com.github.dockerjava.api.DockerClient
import io.ktor.server.application.*
import me.devwckd.server.config.HeimdallConfig
import me.devwckd.server.container_driver.containerDriverModule
import me.devwckd.server.deployment.deploymentModule
import me.devwckd.server.room.roomModule
import org.koin.core.KoinApplication
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureKoin(koin: KoinApplication, config: HeimdallConfig, dockerClient: DockerClient) {
    install(Koin) {

        modules(
            module { single { config } },
            module { single { dockerClient } },
            containerDriverModule,
            deploymentModule,
            roomModule
        )
    }
}