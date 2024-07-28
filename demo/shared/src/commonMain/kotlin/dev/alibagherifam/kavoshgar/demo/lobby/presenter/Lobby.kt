package dev.alibagherifam.kavoshgar.demo.lobby.presenter

import dev.alibagherifam.kavoshgar.Constants
import dev.alibagherifam.kavoshgar.discovery.ServerInformation
import java.net.InetAddress

internal data class Lobby(
    val name: String,
    val address: InetAddress,
    val latency: Long
) {
    /**
     * @return server address in dot-decimal notation (e.g., 192.168.10.64).
     */
    val addressName: String get() = address.toString().drop(1)
}

internal fun ServerInformation.toLobby() = Lobby(
    name,
    address,
    latency
)

internal fun isValidLobbyName(name: String): Boolean {
    return when {
        name.isBlank() -> false
        name.toByteArray().size > Constants.SERVER_NAME_MAX_SIZE -> false
        else -> true
    }
}
