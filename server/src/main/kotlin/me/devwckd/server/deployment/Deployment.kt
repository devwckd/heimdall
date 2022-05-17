package me.devwckd.server.deployment

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.devwckd.server.room.Room
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

/**
 * The threshold between heartbeats in milliseconds before considering
 * a deployment unavailable. After reaching this value, the deployment
 * won't be considered as a viable host for new rooms until
 * a new heartbeat is received.
 */
const val DEPLOYMENT_UNAVAILABILITY_THRESHOLD: Long = 500

/**
 *  The maximum allowed threshold between heartbeats in milliseconds.
 *  After reaching this value, the deployment will be considered dead and
 *  dropped. A kill command will also be dispatched.
 */
const val DEPLOYMENT_MAX_TIMEOUT: Long = 5000

data class Deployment(
    val id: String,
    val address: String,
    val port: Int,
    val deploymentType: String,
    val health: DeploymentHealth,
) {
    val roomsById = ConcurrentHashMap<String, Room>()

    /**
     * Whether the deployment's last heartbeat was sent under
     * [DEPLOYMENT_UNAVAILABILITY_THRESHOLD] milliseconds ago.
     *
     * @return true if the last heartbeat was sent in the last
     * [DEPLOYMENT_UNAVAILABILITY_THRESHOLD] milliseconds. False
     * otherwise.
     */
    fun isAvailable(): Boolean {
        return health.elapsedSinceHeartbeat.inWholeMilliseconds < DEPLOYMENT_UNAVAILABILITY_THRESHOLD
    }

    fun isDead(): Boolean {
        return health.elapsedSinceHeartbeat.inWholeMilliseconds < DEPLOYMENT_MAX_TIMEOUT
    }
}

data class DeploymentHealth(var lastHeartbeat: Instant) {
    private val tpsHistory = ArrayDeque<Double>()
    private val memoryHistory = ArrayDeque<Double>()
    private val cpuHistory = ArrayDeque<Double>()
    private val pingHistory = ArrayDeque<Long>()

    val averageTps: Double
        get() = tpsHistory.average()

    val averageMemory: Double
        get() = memoryHistory.average()

    val averageCpu: Double
        get() = cpuHistory.average()

    val averagePing: Double
        get() = pingHistory.average()

    val elapsedSinceHeartbeat: Duration
        get() = Clock.System.now() - lastHeartbeat

    fun pushTps(tps: Double) {
        tpsHistory.addFirst(tps)
        while (tpsHistory.size > 30) {
            tpsHistory.removeLast()
        }
    }

    fun pushMemory(memory: Double) {
        memoryHistory.addFirst(memory)
        while (memoryHistory.size > 30) {
            memoryHistory.removeLast()
        }
    }

    fun pushCpu(cpu: Double) {
        cpuHistory.addFirst(cpu)
        while (cpuHistory.size > 30) {
            cpuHistory.removeLast()
        }
    }

    fun pushPing(ping: Long) {
        pingHistory.addFirst(ping)
        while (pingHistory.size > 30) {
            pingHistory.removeLast()
        }
    }

}
