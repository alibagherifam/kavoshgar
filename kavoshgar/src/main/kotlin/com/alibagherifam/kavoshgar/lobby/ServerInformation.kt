package com.alibagherifam.kavoshgar.lobby

import java.net.InetAddress
import java.util.Random

/**
 * Data model which contains information that will be shown to clients.
 *
 * @param[name] an arbitrary name.
 * @param[address] internet Protocol address of server.
 * @param[latency] round-trip time for messages sent to server.
 */
data class ServerInformation(
    val name: String,
    val address: InetAddress,
    val latency: Long
) {
    /**
     * @return server address in dot-decimal notation (e.g., 192.168.10.64).
     */
    val addressName: String get() = address.toString().drop(1)
}

fun getRandomServerInformation(): ServerInformation {
    val random = Random()
    return ServerInformation(
        name = "Server #" + random.nextInt(100),
        address = InetAddress.getByName("192.168.1." + random.nextInt(255)),
        latency = random.nextInt(300).toLong()
    )
}
