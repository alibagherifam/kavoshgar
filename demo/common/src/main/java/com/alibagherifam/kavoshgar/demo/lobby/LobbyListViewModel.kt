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
    private val client: KavoshgarClient
) {
    private val _uiState = MutableStateFlow(LobbyListUiState())
    val uiState: StateFlow<LobbyListUiState> get() = _uiState

    private val lobbyTTLs: MutableMap<String, Long> = mutableMapOf()

    init {
        viewModelScope.launch {
            launch {
                client.startDiscovery().collect { newLobby ->
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
            val newList = state.servers.filterNot {
                it.addressName in expiredLobbies
            }
            state.copy(servers = newList)
        }
    }

    private fun addNewLobby(server: ServerInformation) {
        _uiState.update { state ->
            val currentList = state.servers
            val newList = if (currentList.any { it.name == server.name }) {
                currentList.map {
                    if (it.name == server.name) server else it
                }
            } else {
                currentList + server
            }
            state.copy(servers = newList)
        }
    }
}
