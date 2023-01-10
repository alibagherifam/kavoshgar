package com.alibagherifam.kavoshgar.demo.chat

data class ChatNavigationArgs(
    val isLobbyOwner: Boolean,
    val lobbyName: String,
    val lobbyAddress: String? = null
)
