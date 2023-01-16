package com.alibagherifam.kavoshgar.demo.desktop.chat

import com.alibagherifam.kavoshgar.chat.Message

data class ChatUiState(
    val isLookingForClient: Boolean = false,
    val messages: List<Message> = emptyList(),
    val messageInputValue: String = "",
    val isConnectionLost: Boolean = false
)
