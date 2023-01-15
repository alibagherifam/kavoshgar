package com.alibagherifam.kavoshgar.chat

import com.alibagherifam.kavoshgar.Constants
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class ClientChatSocketProvider(
    private val serverAddress: InetAddress
) : ChatSocketProvider {
    override fun openSocket(): Socket = Socket().apply {
        connect(InetSocketAddress(serverAddress, Constants.CHAT_SERVER_PORT))
    }
}
