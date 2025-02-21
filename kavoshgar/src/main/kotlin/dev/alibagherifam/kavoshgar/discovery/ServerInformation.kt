package dev.alibagherifam.kavoshgar.discovery

import java.net.InetAddress

/**
 * Server information that will be shown to clients.
 *
 * @param[address] IP address of server.
 * @param[latency] round-trip time for messages sent to server.
 * @param[payload] an arbitrary payload.
 */
class ServerInformation(
    val address: InetAddress,
    val latency: Long,
    val payload: ByteArray
) {
    override fun toString(): String =
        "ServerInformation(address = $address, latency = $latency, payload = $payload)"
}
