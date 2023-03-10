package dev.alibagherifam.kavoshgar.messenger

import dev.alibagherifam.kavoshgar.Constants
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class ClientSocketProvider(
    private val serverAddress: InetAddress
) : SocketProvider {
    override fun openSocket(): Socket = Socket().apply {
        connect(InetSocketAddress(serverAddress, Constants.MESSAGING_PORT))
    }
}
