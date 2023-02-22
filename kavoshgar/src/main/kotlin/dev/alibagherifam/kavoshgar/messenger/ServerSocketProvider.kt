package dev.alibagherifam.kavoshgar.messenger

import dev.alibagherifam.kavoshgar.Constants
import java.net.ServerSocket
import java.net.Socket

class ServerSocketProvider : SocketProvider {
    override fun openSocket(): Socket {
        val serverSocket = ServerSocket(Constants.MESSAGING_PORT)
        return serverSocket.accept()
    }
}
