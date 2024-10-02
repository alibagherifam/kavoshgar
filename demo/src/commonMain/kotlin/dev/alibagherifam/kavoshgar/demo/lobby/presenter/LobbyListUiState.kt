package dev.alibagherifam.kavoshgar.demo.lobby.presenter

import dev.alibagherifam.kavoshgar.demo.lobby.model.Lobby

internal data class LobbyListUiState(
    val lobbies: List<Lobby> = emptyList(),
    val selectedLobby: Lobby? = null
)
