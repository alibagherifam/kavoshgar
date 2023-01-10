package com.alibagherifam.kavoshgar.chat

import com.alibagherifam.kavoshgar.Constants
import java.net.ServerSocket
import java.net.Socket

class ServerChatSocketProvider : ChatSocketProvider {
    override fun openSocket(): Socket {
        val serverSocket = ServerSocket(Constants.CHAT_SERVER_PORT)
        return serverSocket.accept()
    }
}
