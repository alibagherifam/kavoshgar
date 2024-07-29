package dev.alibagherifam.kavoshgar

internal object Constants {
    const val BROADCAST_ADDRESS = "255.255.255.255"
    const val MESSAGING_PORT = 5200
    const val MESSAGING_INTERVALS = 500L
    const val SERVER_NAME_MAX_SIZE = 64
    const val ADVERTISEMENT_PORT = 3600
    const val ADVERTISEMENT_INTERVALS = 1000L
    const val LOBBY_DEFAULT_TTL = ADVERTISEMENT_INTERVALS * 5
    const val PING_TIMEOUT = (ADVERTISEMENT_INTERVALS * 2).toInt()
}
