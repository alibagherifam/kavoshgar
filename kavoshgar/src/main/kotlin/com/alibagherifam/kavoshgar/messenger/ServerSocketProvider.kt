package com.alibagherifam.kavoshgar.messenger

import com.alibagherifam.kavoshgar.Constants
import java.net.ServerSocket
import java.net.Socket

class ServerSocketProvider : SocketProvider {
    override fun openSocket(): Socket {
        val serverSocket = ServerSocket(Constants.CHAT_SERVER_PORT)
        return serverSocket.accept()
    }
}
