package dev.alibagherifam.kavoshgar.demo.lobby.presenter

internal data class LobbyListUiState(
    val lobbies: List<Lobby> = emptyList(),
    val selectedLobby: Lobby? = null
)
