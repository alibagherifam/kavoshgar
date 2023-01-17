package com.alibagherifam.kavoshgar

object Constants {
    const val BROADCAST_ADDRESS = "255.255.255.255"
    const val MESSAGING_PORT = 5200
    const val SERVER_DISCOVERY_PORT = 3600
    const val SERVER_NAME_MAX_SIZE = 64
    const val DISCOVERY_PACKET_SIZE = 1
    const val DISCOVERY_INTERVALS = 1000L
    const val MESSAGING_INTERVALS = 500L
    const val LOBBY_DEFAULT_TTL = DISCOVERY_INTERVALS * 5
    const val PING_TIME_OUT = DISCOVERY_INTERVALS * 2
}
