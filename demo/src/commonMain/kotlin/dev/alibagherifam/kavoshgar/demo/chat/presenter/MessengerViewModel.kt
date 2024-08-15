package dev.alibagherifam.kavoshgar.demo.chat.presenter

import dev.alibagherifam.kavoshgar.demo.BaseViewModel
import dev.alibagherifam.kavoshgar.discovery.KavoshgarServer
import dev.alibagherifam.kavoshgar.logger.Log
import dev.alibagherifam.kavoshgar.messenger.MessengerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class MessengerViewModel(
    viewModelScope: CoroutineScope,
    private val messenger: MessengerService,
    private val server: KavoshgarServer? = null
) : BaseViewModel<ChatUiState>(viewModelScope, initialState = ChatUiState()) {
    private var serverAdvertisementJob: Job? = null

    init {
        launchInUi {
            receiveMessages()
        }
        if (server != null) {
            serverAdvertisementJob = viewModelScope.launch {
                startServerAdvertisement()
            }
        }
    }

    fun changeMessageInputValue(newValue: String) {
        _uiState.update {
            it.copy(messageInputValue = newValue)
        }
    }

    fun sendMessage() = launchInUi {
        val message = uiState.value.messageInputValue
        messenger.sendMessage(message)
        _uiState.update {
            it.copy(
                messages = addMessageToList(message, isFromUser = true),
                messageInputValue = ""
            )
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
                if (serverAdvertisementJob?.isActive == true) {
                    stopServerAdvertisement()
                }
            } else {
                _uiState.update {
                    it.copy(messages = addMessageToList(message, isFromUser = false))
                }
            }
        }
    }

    private suspend fun startServerAdvertisement() {
        _uiState.update {
            it.copy(isLookingForClient = true)
        }
        server!!.advertisePresence()
    }

    private fun stopServerAdvertisement() {
        serverAdvertisementJob!!.cancel()
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
            content = message
        )
        return uiState.value.messages + newMessage
    }
}
