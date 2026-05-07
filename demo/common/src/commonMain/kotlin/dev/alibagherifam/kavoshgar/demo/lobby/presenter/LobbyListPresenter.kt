package dev.alibagherifam.kavoshgar.demo.lobby.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alibagherifam.kavoshgar.Constants
import dev.alibagherifam.kavoshgar.demo.lobby.model.Lobby
import dev.alibagherifam.kavoshgar.demo.lobby.model.toLobby
import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListUiEvent.LobbyClick
import dev.alibagherifam.kavoshgar.discovery.KavoshgarClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class LobbyListPresenter internal constructor(
    private val client: KavoshgarClient
) : ViewModel() {
    private val lobbyExpirationTimes: MutableMap<String, Long> = mutableMapOf()

    val uiState: StateFlow<LobbyListUiState>
        field = MutableStateFlow(LobbyListUiState())

    init {
        viewModelScope.launch {
            client
                .startServerDiscovery()
                .map { serverInfo -> serverInfo.toLobby() }
                .collect { lobby ->
                    upsertLobby(lobby)
                    scheduleExpiration(lobby)
                }
        }

        viewModelScope.launch {
            while (true) {
                removeExpiredLobbies()
                delay(Constants.ADVERTISEMENT_INTERVALS)
            }
        }
    }

    val eventSink: (LobbyListUiEvent) -> Unit = { event ->
        when (event) {
            is LobbyClick -> {
                selectLobby(event.lobby)
            }
        }
    }

    private fun selectLobby(lobby: Lobby) {
        uiState.update {
            it.copy(selectedLobby = if (lobby.name == it.selectedLobby?.name) null else lobby)
        }
    }

    private fun upsertLobby(lobby: Lobby) {
        uiState.update { state ->
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

        uiState.update { state ->
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
