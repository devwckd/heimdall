package me.devwckd.server.deployment

import org.koin.dsl.module

val deploymentModule = module {
    single { DeploymentManager() }
}