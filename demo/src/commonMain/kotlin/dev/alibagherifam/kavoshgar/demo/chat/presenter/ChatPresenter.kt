package dev.alibagherifam.kavoshgar.demo.chat.presenter

import dev.alibagherifam.kavoshgar.demo.Presenter
import dev.alibagherifam.kavoshgar.demo.chat.model.Message
import dev.alibagherifam.kavoshgar.demo.chat.presenter.ChatUiEvent.MessageSend
import dev.alibagherifam.kavoshgar.discovery.KavoshgarServer
import dev.alibagherifam.kavoshgar.messenger.MessengerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import de.halfbit.logger.e as logError

internal class ChatPresenter(
    private val messenger: MessengerService,
    private val server: KavoshgarServer? = null
) : Presenter<ChatUiState, ChatUiEvent>() {
    private var serverAdvertisementJob: Job? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    override val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    override val eventSink: (ChatUiEvent) -> Unit = { event ->
        when (event) {
            is MessageSend -> {
                presenterScope.launch {
                    sendMessage(event.message)
                }
            }
        }
    }

    init {
        presenterScope.launch {
            receiveMessages()
        }
        if (server != null) {
            serverAdvertisementJob = presenterScope.launch {
                startServerAdvertisement()
            }
        }
    }

    private suspend fun sendMessage(message: String) {
        messenger.sendMessage(message)
        _uiState.update {
            it.copy(
                messages = addMessageToList(message, isFromUser = true),
            )
        }
    }

    private suspend fun receiveMessages() {
        messenger.receiveMessages().catch {
            logError(tag = "ChatViewModel", err = it)
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
