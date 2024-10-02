package dev.alibagherifam.kavoshgar.demo.chat.presenter

import dev.alibagherifam.kavoshgar.demo.chat.model.Message

internal data class ChatUiState(
    val isLookingForClient: Boolean = false,
    val messages: List<Message> = emptyList(),
    val isConnectionLost: Boolean = false
)
