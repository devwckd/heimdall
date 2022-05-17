package me.devwckd.server.room

import me.devwckd.server.deployment.Deployment
import me.devwckd.server.deployment.DeploymentManager

class RoomManager(
    private val deploymentManager: DeploymentManager
) {

    fun getById(deploymentId: String, roomId: String): Room? {
        return deploymentManager.getById(deploymentId)?.roomsById?.get(roomId)
    }

}