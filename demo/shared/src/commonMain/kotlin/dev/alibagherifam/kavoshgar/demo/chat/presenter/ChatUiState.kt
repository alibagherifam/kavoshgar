package dev.alibagherifam.kavoshgar.demo.chat.presenter

internal data class ChatUiState(
    val isLookingForClient: Boolean = false,
    val messages: List<Message> = emptyList(),
    val messageInputValue: String = "",
    val isConnectionLost: Boolean = false
)
