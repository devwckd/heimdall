package me.devwckd.server.room

import org.koin.dsl.module

val roomModule = module {
    single { RoomManager(get()) }
}