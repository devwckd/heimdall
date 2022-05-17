package me.devwckd.plugin

import com.sun.management.OperatingSystemMXBean
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import me.devwckd.client.HeimdallClient
import me.devwckd.shared.deployment.DeploymentHeartbeat
import org.bukkit.plugin.java.JavaPlugin
import java.lang.management.ManagementFactory

class HeimdallSpigotPlugin : JavaPlugin() {

    private val heimdallClient = HeimdallClient("http://209.145.52.190:8080/")

    override fun onEnable() {
        server.scheduler.runTaskTimer(this, Runnable {
            runBlocking {
                runCatching {
                    heimdallClient.deployment.heartbeat(
                        DeploymentHeartbeat(
                            "1",
                            20.0,
                            Runtime.getRuntime().totalMemory().toDouble(),
                            ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java).processCpuLoad,
                            Clock.System.now()
                        )
                    )
                }.onFailure {
                    logger.warning("couldn't connect to heimdall via 'http://209.145.52.190:8080/' ${it.message}.")
                }
            }
        }, 0, 20L)
    }

}