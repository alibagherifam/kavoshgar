package com.alibagherifam.kavoshgar.demo.lobby

data class LobbyListUiState(
    val lobbies: List<Lobby> = emptyList(),
    val selectedLobby: Lobby? = null
)
