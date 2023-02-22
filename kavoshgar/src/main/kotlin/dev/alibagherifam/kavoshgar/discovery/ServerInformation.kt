package dev.alibagherifam.kavoshgar.discovery

import java.net.InetAddress

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
)
