package com.alibagherifam.kavoshgar.demo.lobby

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.lobby.KavoshgarClient
import com.alibagherifam.kavoshgar.lobby.ServerInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LobbyListViewModel(
    viewModelScope: CoroutineScope,
    private val lobbyDiscoveryRepo: KavoshgarClient
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
                lobbyDiscoveryRepo.discoveredServerFlow().collect { newLobby ->
                    lobbyTTLs[newLobby.addressName] = getNextTTL()
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

    fun selectLobby(selectedServer: ServerInformation) {
        _uiState.update {
            it.copy(selectedServer = selectedServer)
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

    private fun addNewLobby(information: ServerInformation) {
        _uiState.update { state ->
            val currentList = state.lobbies
            val newList = if (currentList.any { it.name == information.name }) {
                currentList.map {
                    if (it.name == information.name) information else it
                }
            } else {
                currentList + information
            }
            state.copy(lobbies = newList)
        }
    }
}
