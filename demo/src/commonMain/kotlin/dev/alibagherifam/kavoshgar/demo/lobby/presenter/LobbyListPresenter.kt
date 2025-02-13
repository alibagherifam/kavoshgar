package dev.alibagherifam.kavoshgar.demo.lobby.presenter

import dev.alibagherifam.kavoshgar.Constants
import dev.alibagherifam.kavoshgar.demo.Presenter
import dev.alibagherifam.kavoshgar.demo.lobby.model.Lobby
import dev.alibagherifam.kavoshgar.demo.lobby.model.toLobby
import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListUiEvent.LobbySelection
import dev.alibagherifam.kavoshgar.discovery.KavoshgarClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LobbyListPresenter internal constructor(
    private val client: KavoshgarClient
) : Presenter<LobbyListUiState, LobbyListUiEvent>() {
    private val lobbyTTLs: MutableMap<String, Long> = mutableMapOf()

    private val _uiState = MutableStateFlow(LobbyListUiState())
    override val uiState: StateFlow<LobbyListUiState> = _uiState.asStateFlow()

    override val eventSink: (LobbyListUiEvent) -> Unit = { event ->
        when (event) {
            is LobbySelection -> {
                selectLobby(event.lobby)
            }
        }
    }

    init {
        presenterScope.launch {
            client.startServerDiscovery().collect { serverInformation ->
                val lobby = serverInformation.toLobby()
                lobbyTTLs[lobby.addressName] = getNextTTL()
                addNewLobby(lobby)
            }
        }

        presenterScope.launch {
            while (true) {
                removeExpiredLobbies()
                delay(Constants.ADVERTISEMENT_INTERVALS)
            }
        }
    }

    private fun selectLobby(lobby: Lobby) {
        _uiState.update {
            it.copy(selectedLobby = lobby)
        }
    }

    private fun getNextTTL(): Long =
        System.currentTimeMillis() + Constants.LOBBY_DEFAULT_TTL

    private fun removeExpiredLobbies() {
        val expiredLobbies = lobbyTTLs.filter {
            it.value - System.currentTimeMillis() < 0
        }.keys
        expiredLobbies.forEach { lobbyTTLs.remove(it) }
        _uiState.update { state ->
            val newList = state.lobbies.filterNot {
                it.addressName in expiredLobbies
            }
            state.copy(lobbies = newList)
        }
    }

    private fun addNewLobby(lobby: Lobby) {
        _uiState.update { state ->
            val currentList = state.lobbies
            val newList = if (currentList.any { it.name == lobby.name }) {
                currentList.map {
                    if (it.name == lobby.name) lobby else it
                }
            } else {
                currentList + lobby
            }
            state.copy(lobbies = newList)
        }
    }
}
