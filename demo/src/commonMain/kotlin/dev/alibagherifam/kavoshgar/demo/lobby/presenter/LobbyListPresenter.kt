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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class LobbyListPresenter internal constructor(
    private val client: KavoshgarClient
) : Presenter<LobbyListUiState, LobbyListUiEvent>() {
    private val lobbyExpirationTimes: MutableMap<String, Long> = mutableMapOf()

    private val _uiState = MutableStateFlow(LobbyListUiState())
    override val uiState: StateFlow<LobbyListUiState> = _uiState.asStateFlow()

    init {
        presenterScope.launch {
            client
                .startServerDiscovery()
                .map { serverInfo -> serverInfo.toLobby() }
                .collect { lobby ->
                    upsertLobby(lobby)
                    scheduleExpiration(lobby)
                }
        }

        presenterScope.launch {
            while (true) {
                removeExpiredLobbies()
                delay(Constants.ADVERTISEMENT_INTERVALS)
            }
        }
    }

    override val eventSink: (LobbyListUiEvent) -> Unit = { event ->
        when (event) {
            is LobbySelection -> {
                selectLobby(event.lobby)
            }
        }
    }

    private fun selectLobby(lobby: Lobby) {
        _uiState.update {
            it.copy(selectedLobby = lobby)
        }
    }

    private fun upsertLobby(lobby: Lobby) {
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

    private fun scheduleExpiration(lobby: Lobby) {
        lobbyExpirationTimes[lobby.addressName] = now() + LOBBY_TTL
    }

    private fun removeExpiredLobbies() {
        val now = now()
        val expiredLobbies = lobbyExpirationTimes
            .filterValues { expTime -> expTime < now }
            .keys

        expiredLobbies.forEach {
            lobbyExpirationTimes.remove(it)
        }

        _uiState.update { state ->
            val newList = state.lobbies.filterNot {
                it.addressName in expiredLobbies
            }
            state.copy(lobbies = newList)
        }
    }

    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

    companion object {
        const val LOBBY_TTL = Constants.ADVERTISEMENT_INTERVALS * 5
    }
}
