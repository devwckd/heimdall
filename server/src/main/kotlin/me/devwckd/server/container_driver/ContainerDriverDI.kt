package me.devwckd.server.container_driver

import me.devwckd.server.container_driver.impl.DockerContainerDriver
import org.koin.dsl.module

val containerDriverModule = module {
    single<ContainerDriver> { DockerContainerDriver(get()) }
}