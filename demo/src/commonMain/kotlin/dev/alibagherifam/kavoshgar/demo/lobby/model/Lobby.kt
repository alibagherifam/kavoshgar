package dev.alibagherifam.kavoshgar.demo.lobby.model

import dev.alibagherifam.kavoshgar.Constants
import dev.alibagherifam.kavoshgar.discovery.ServerInformation
import java.net.InetAddress

data class Lobby(
    val name: String,
    val address: InetAddress,
    val latency: Long
) {
    /**
     * @return server address in dot-decimal notation (e.g., 192.168.10.64).
     */
    val addressName: String get() = address.hostAddress
}

internal fun ServerInformation.toLobby() =
    Lobby(
        name = String(payload),
        address = address,
        latency = latency
    )

internal fun isValidLobbyName(name: String): Boolean {
    return when {
        name.isBlank() -> false
        name.toByteArray().size > Constants.ADVERTISEMENT_PACKET_MAX_SIZE -> false
        else -> true
    }
}

object FakeLobbyFactory {
    fun create(): Lobby = createList().first()

    fun createList(): List<Lobby> =
        listOf(
            Lobby(
                name = "Server #1",
                address = InetAddress.getByName("111.225.179.31"),
                latency = 32
            ),
            Lobby(
                name = "Server #2",
                address = InetAddress.getByName("51.97.225.106"),
                latency = 211
            ),
            Lobby(
                name = "Server #3",
                address = InetAddress.getByName("194.169.12.147"),
                latency = 9
            ),
            Lobby(
                name = "Server #4",
                address = InetAddress.getByName("233.32.222.209"),
                latency = 153
            ),
            Lobby(
                name = "Server #5",
                address = InetAddress.getByName("147.221.157.105"),
                latency = 46
            )
        )
}
