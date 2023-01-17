package com.alibagherifam.kavoshgar.demo.lobby

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.discovery.KavoshgarClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LobbyListViewModel(
    viewModelScope: CoroutineScope,
    private val client: KavoshgarClient
) {
    private val _uiState = MutableStateFlow(LobbyListUiState())
    val uiState: StateFlow<LobbyListUiState> get() = _uiState

    private val lobbyTTLs: MutableMap<String, Long> = mutableMapOf()

    init {
        viewModelScope.launch {
            launch {
                client.startDiscovery().collect { serverInformation ->
                    val lobby = serverInformation.toLobby()
                    lobbyTTLs[lobby.addressName] = getNextTTL()
                    addNewLobby(lobby)
                }
            }
            launch {
                while (isActive) {
                    removeExpiredLobbies()
                    delay(Constants.DISCOVERY_INTERVALS)
                }
            }
        }
    }

    fun selectLobby(lobby: Lobby) {
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
