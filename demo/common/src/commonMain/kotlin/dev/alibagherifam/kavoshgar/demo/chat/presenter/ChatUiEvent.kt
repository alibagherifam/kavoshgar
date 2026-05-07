package dev.alibagherifam.kavoshgar.demo.chat.presenter

sealed interface ChatUiEvent {
    data class MessageSend(val message: String) : ChatUiEvent
}
