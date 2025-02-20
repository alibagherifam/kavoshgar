package dev.alibagherifam.kavoshgar.messenger

import dev.alibagherifam.kavoshgar.Constants
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

interface SocketProvider {
    fun openSocket(): Socket
}

class ServerSocketProvider : SocketProvider {
    override fun openSocket(): Socket {
        val serverSocket = ServerSocket(Constants.MESSAGING_PORT)
        return serverSocket.accept()
    }
}

class ClientSocketProvider(
    private val serverAddress: InetAddress
) : SocketProvider {
    override fun openSocket(): Socket = Socket().apply {
        connect(InetSocketAddress(serverAddress, Constants.MESSAGING_PORT))
    }
}
