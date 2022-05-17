package me.devwckd.shared.room

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RoomHeartbeat(
    val deploymentId: String,
    val roomId: String,

    val currentPlayers: Int,
    val roomState: String,

    val sentAt: LocalDateTime
)
