package me.devwckd.server

import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.devwckd.shared.deployment.DeploymentHeartbeat

//
//import me.devwckd.heimdall.server.deployment.Deployment
//import org.apache.logging.log4j.LogManager
//import org.apache.logging.log4j.Logger
//import java.lang.Runnable
//import java.util.concurrent.Executors
//import java.util.concurrent.TimeUnit
//
//class Watchdog(val servers: () -> Collection<Deployment> = { emptyList() }) : Runnable {
//    private val logger: Logger = LogManager.getLogger("server-watchdog")
//
//    override fun run() {
//        val servers = servers()
//
//        servers.filter(Deployment::isDead).forEach {
//            logger.warn(
//                "server {} timed out after {}ms and will be dropped",
//                it.id, it.health.elapsedSinceHeartbeat().toMillis()
//            )
//            // TODO: dispatch kill command
//        }
//
//        servers.filter { !it.isAvailable() && !it.isDead() }.forEach {
//            logger.warn(
//                "server {} hasn't sent a heartbeat in {}ms",
//                it.id, it.health.elapsedSinceHeartbeat().toMillis()
//            )
//        }
//    }
//}
//
//fun startWatchdog() {
//    Executors
//        .newSingleThreadScheduledExecutor()
//        .schedule(Watchdog(servers = { listOf() }), 10, TimeUnit.MILLISECONDS)
//}

fun main() {
    println(Json.encodeToString(DeploymentHeartbeat("", 0.0, 0.0, 0.0, Clock.System.now())))
}