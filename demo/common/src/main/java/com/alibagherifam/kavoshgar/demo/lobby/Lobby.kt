package com.alibagherifam.kavoshgar.demo.lobby

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.discovery.ServerInformation
import java.net.InetAddress

data class Lobby(
    val name: String,
    val address: InetAddress,
    val latency: Long
) {
    /**
     * @return server address in dot-decimal notation (e.g., 192.168.10.64).
     */
    val addressName: String get() = address.toString().drop(1)
}

fun ServerInformation.toLobby() = Lobby(
    name,
    address,
    latency
)

fun isValidLobbyName(name: String): Boolean {
    return when {
        name.isBlank() -> false
        name.toByteArray().size > Constants.SERVER_NAME_MAX_SIZE -> false
        else -> true
    }
}
