package com.alibagherifam.kavoshgar.demo.chat

import com.alibagherifam.kavoshgar.messenger.MessengerService
import com.alibagherifam.kavoshgar.discovery.KavoshgarServer
import com.alibagherifam.kavoshgar.logger.Log
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

class MessengerViewModel(
    private val viewModelScope: CoroutineScope,
    private val messenger: MessengerService,
    private val server: KavoshgarServer? = null
) {
    private var serverAdvertismentJob: Job? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> get() = _uiState

    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    init {
        viewModelScope.launch {
            receiveMessages()
        }
        if (server != null) {
            serverAdvertismentJob = viewModelScope.launch {
                startServerAdvertisment()
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
            messenger.sendMessage(message)
            _uiState.update {
                it.copy(
                    messages = addMessageToList(message, isFromUser = true),
                    messageInputValue = ""
                )
            }
        }
    }

    private suspend fun receiveMessages() {
        messenger.receiveMessages().catch {
            Log.e(tag = "ChatViewModel", error = it)
            _uiState.update { state ->
                state.copy(isConnectionLost = true)
            }
        }.collect { message ->
            if (message.isBlank()) {
                if (serverAdvertismentJob?.isActive == true) {
                    stopServerAdvertisment()
                }
            } else {
                _uiState.update {
                    it.copy(messages = addMessageToList(message, isFromUser = false))
                }
            }
        }
    }

    private suspend fun startServerAdvertisment() {
        _uiState.update {
            it.copy(isLookingForClient = true)
        }
        server!!.advertisePresence()
    }

    private fun stopServerAdvertisment() {
        serverAdvertismentJob!!.cancel()
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
