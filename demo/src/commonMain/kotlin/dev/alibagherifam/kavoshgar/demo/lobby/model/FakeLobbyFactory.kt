package dev.alibagherifam.kavoshgar.demo.lobby.model

import java.net.InetAddress
import kotlin.random.Random

object FakeLobbyFactory {
    fun create(): Lobby =
        Lobby(
            name = "Server #" + Random.nextInt(100),
            address = InetAddress.getByName("192.168.1." + Random.nextInt(255)),
            latency = Random.nextInt(300).toLong()
        )
}
