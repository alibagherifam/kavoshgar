package dev.alibagherifam.kavoshgar.demo.lobby

import java.net.InetAddress
import java.util.Random

fun getRandomLobbies(): Lobby {
    val random = Random()
    return Lobby(
        name = "Server #" + random.nextInt(100),
        address = InetAddress.getByName("192.168.1." + random.nextInt(255)),
        latency = random.nextInt(300).toLong()
    )
}
