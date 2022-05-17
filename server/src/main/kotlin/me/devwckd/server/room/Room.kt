package me.devwckd.server.room

import kotlinx.datetime.Instant

data class Room(
    val id: String,
    val hostId: String,
    val maxPlayers: Int,
    val roomType: String,
    val variant: String,
    val status: RoomStatus,
)

data class RoomStatus(
    var currentPlayers: Int,
    var state: String,
    var locked: Boolean,
    var lastHeartbeat: Instant,
)