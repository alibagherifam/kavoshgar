package com.alibagherifam.kavoshgar.demo.desktop.chat

import com.alibagherifam.kavoshgar.chat.ChatRepository
import com.alibagherifam.kavoshgar.chat.Message
import com.alibagherifam.kavoshgar.lobby.KavoshgarServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatViewModel(
    private val viewModelScope: CoroutineScope,
    private val chatRepository: ChatRepository,
    private val server: KavoshgarServer? = null
) {
    private var discoveryReplyingJob: Job? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> get() = _uiState

    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    init {
        viewModelScope.launch {
            launch {
                receiveMessages()
            }
            if (server != null) {
                discoveryReplyingJob = launch {
                    startDiscoveryReplying()
                }
            }
        }
    }

    fun changeMessageInputValue(newValue: String) {
        _uiState.update {
            it.copy(messageInputValue = newValue)
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            val message = uiState.value.messageInputValue
            chatRepository.sendMessage(message)
            _uiState.update {
                it.copy(
                    messages = addMessageToList(message, isFromUser = true),
                    messageInputValue = ""
                )
            }
        }
    }

    private suspend fun receiveMessages() {
        chatRepository.receiveMessages().catch {
            it.printStackTrace()
            _uiState.update { state ->
                state.copy(isConnectionLost = true)
            }
        }.collect { message ->
            if (message.isBlank()) {
                if (discoveryReplyingJob?.isActive == true) {
                    stopDiscoveryReplying()
                }
            } else {
                _uiState.update {
                    it.copy(messages = addMessageToList(message, isFromUser = false))
                }
            }
        }
    }

    private suspend fun startDiscoveryReplying() {
        _uiState.update {
            it.copy(isLookingForClient = true)
        }
        server!!.startDiscoveryReplying()
    }

    private fun stopDiscoveryReplying() {
        discoveryReplyingJob!!.cancel()
        _uiState.update {
            it.copy(isLookingForClient = false)
        }
    }

    private fun addMessageToList(
        message: String,
        isFromUser: Boolean
    ): List<Message> {
        val newMessage = Message(
            isMine = isFromUser,
            content = message,
            timestamp = timeFormatter.format(Date(System.currentTimeMillis()))
        )
        return uiState.value.messages + newMessage
    }
}
