package com.alibagherifam.kavoshgar.lobby

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
) {
    /**
     * @return server address in dot-decimal notation (e.g., 192.168.10.64).
     */
    val addressName: String get() = address.toString().drop(1)
}
