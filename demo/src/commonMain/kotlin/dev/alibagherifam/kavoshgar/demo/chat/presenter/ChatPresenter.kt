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

class ChatPresenter internal constructor(
    private val lobbyName: String,
    private val messenger: MessengerService,
    private val server: KavoshgarServer? = null
) : Presenter<ChatUiState, ChatUiEvent>() {
    private var serverAdvertisementJob: Job? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    override val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

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

    override val eventSink: (ChatUiEvent) -> Unit = { event ->
        when (event) {
            is MessageSend -> {
                presenterScope.launch {
                    sendMessage(event.message)
                }
            }
        }
    }

    private suspend fun sendMessage(message: String) {
        messenger.sendMessage(message)
        _uiState.update {
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
        _uiState.update {
            it.copy(isLookingForClient = true)
        }
        server!!.advertisePresence(lobbyName.toByteArray())
    }

    private fun stopServerAdvertisement() {
        serverAdvertisementJob!!.cancel()
        _uiState.update {
            it.copy(isLookingForClient = false)
        }
    }
}
