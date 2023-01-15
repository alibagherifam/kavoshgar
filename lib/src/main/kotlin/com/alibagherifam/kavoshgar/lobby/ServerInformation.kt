package com.alibagherifam.kavoshgar.lobby

import java.net.InetAddress

data class ServerInformation(
    val name: String,
    val address: InetAddress,
    val latency: Long
) {
    val addressName: String get() = address.toString().drop(1)
}
