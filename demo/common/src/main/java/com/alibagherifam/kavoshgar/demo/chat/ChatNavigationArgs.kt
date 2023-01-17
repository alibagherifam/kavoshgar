package com.alibagherifam.kavoshgar.demo.chat

import java.net.InetAddress

data class ChatNavigationArgs(
    val isLobbyOwner: Boolean,
    val lobbyName: String,
    val lobbyAddress: InetAddress? = null
)

