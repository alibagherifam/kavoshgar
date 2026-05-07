package dev.alibagherifam.kavoshgar.demo.chat.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alibagherifam.kavoshgar.demo.chat.model.Message
import dev.alibagherifam.kavoshgar.demo.chat.presenter.ChatUiEvent.MessageSend
import dev.alibagherifam.kavoshgar.discovery.KavoshgarServer
import dev.alibagherifam.kavoshgar.messenger.MessengerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import de.halfbit.logger.e as logError

class ChatPresenter internal constructor(
    private val lobbyName: String,
    private val messenger: MessengerService,
    private val server: KavoshgarServer? = null
) : ViewModel() {
    private var serverAdvertisementJob: Job? = null

    val uiState: StateFlow<ChatUiState>
        field = MutableStateFlow(ChatUiState())

    init {
        viewModelScope.launch {
            receiveMessages()
        }

        if (server != null) {
            serverAdvertisementJob = viewModelScope.launch {
                startServerAdvertisement()
            }
        }
    }

    val eventSink: (ChatUiEvent) -> Unit = { event ->
        when (event) {
            is MessageSend -> {
                viewModelScope.launch {
                    sendMessage(event.message)
                }
            }
        }
    }

    private suspend fun sendMessage(message: String) {
        messenger.sendMessage(message)
        uiState.update {
            val newMessage = Message(
                isMine = true,
                content = message
            )
            it.copy(messages = it.messages + newMessage)
        }
    }

    private suspend fun receiveMessages() {
        messenger
            .receiveMessages()
            .catch {
                logError(tag = "ChatPresenter", err = it)
                uiState.update { state ->
                    state.copy(isConnectionLost = true)
                }
            }.collect { message ->
                if (message.isBlank()) {
                    if (serverAdvertisementJob?.isActive == true) {
                        stopServerAdvertisement()
                    }
                } else {
                    uiState.update {
                        val newMessage = Message(
                            isMine = false,
                            content = message
                        )
                        it.copy(messages = it.messages + newMessage)
                    }
                }
            }
    }

    private suspend fun startServerAdvertisement() {
        uiState.update {
            it.copy(isLookingForClient = true)
        }
        server!!.advertisePresence(lobbyName.toByteArray())
    }

    private fun stopServerAdvertisement() {
        serverAdvertisementJob!!.cancel()
        uiState.update {
            it.copy(isLookingForClient = false)
        }
    }
}
