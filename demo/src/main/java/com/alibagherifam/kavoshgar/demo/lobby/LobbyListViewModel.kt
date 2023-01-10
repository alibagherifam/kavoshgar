package com.alibagherifam.kavoshgar.demo.lobby

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.lobby.Lobby
import com.alibagherifam.kavoshgar.lobby.LobbyDiscoveryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LobbyListViewModel(
    viewModelScope: CoroutineScope,
    private val lobbyDiscoveryRepo: LobbyDiscoveryRepository
) {
    private val _uiState = MutableStateFlow(LobbyListUiState())
    val uiState: StateFlow<LobbyListUiState> get() = _uiState

    private val lobbyTTLs: MutableMap<String, Long> = mutableMapOf()

    init {
        viewModelScope.launch {
            launch {
                lobbyDiscoveryRepo.startDiscovery()
            }
            launch {
                lobbyDiscoveryRepo.getDiscoveredLobbies().collect { newLobby ->
                    lobbyTTLs[newLobby.address] = getNextTTL()
                    addNewLobby(newLobby)
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

    fun selectLobby(selectedLobby: Lobby) {
        _uiState.update {
            it.copy(selectedLobby = selectedLobby)
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
                it.address in expiredLobbies
            }
            state.copy(lobbies = newList)
        }
    }

    private fun addNewLobby(newLobby: Lobby) {
        _uiState.update { state ->
            val currentList = state.lobbies
            val newList = if (currentList.any { it.name == newLobby.name }) {
                currentList.map {
                    if (it.name == newLobby.name) newLobby else it
                }
            } else {
                currentList + newLobby
            }
            state.copy(lobbies = newList)
        }
    }
}
