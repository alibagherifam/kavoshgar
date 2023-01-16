package com.alibagherifam.kavoshgar.demo.desktop.chat

import java.net.InetAddress

data class ChatNavigationArgs(
    val isLobbyOwner: Boolean,
    val lobbyName: String,
    val serverAddress: InetAddress? = null
)

