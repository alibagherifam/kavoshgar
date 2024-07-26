package dev.alibagherifam.kavoshgar.demo.lobby

import dev.alibagherifam.kavoshgar.Constants
import dev.alibagherifam.kavoshgar.demo.BaseViewModel
import dev.alibagherifam.kavoshgar.discovery.KavoshgarClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update

class LobbyListViewModel(
    viewModelScope: CoroutineScope,
    private val client: KavoshgarClient
) : BaseViewModel<LobbyListUiState>(viewModelScope, initialState = LobbyListUiState()) {

    private val lobbyTTLs: MutableMap<String, Long> = mutableMapOf()

    init {
        launchInUi {
            client.startServerDiscovery().collect { serverInformation ->
                val lobby = serverInformation.toLobby()
                lobbyTTLs[lobby.addressName] = getNextTTL()
                addNewLobby(lobby)
            }
        }
        launchInUi {
            while (true) {
                removeExpiredLobbies()
                delay(Constants.ADVERTISEMENT_INTERVALS)
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
