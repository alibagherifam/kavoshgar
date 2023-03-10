package dev.alibagherifam.kavoshgar

object Constants {
    const val BROADCAST_ADDRESS = "255.255.255.255"
    const val MESSAGING_PORT = 5200
    const val MESSAGING_INTERVALS = 500L
    const val SERVER_NAME_MAX_SIZE = 64
    const val ADVERTISMENT_PORT = 3600
    const val ADVERTISMENT_INTERVALS = 1000L
    const val LOBBY_DEFAULT_TTL = ADVERTISMENT_INTERVALS * 5
    const val PING_TIMEOUT = (ADVERTISMENT_INTERVALS * 2).toInt()
}
