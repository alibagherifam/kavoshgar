package com.alibagherifam.kavoshgar.demo.lobby

import com.alibagherifam.kavoshgar.lobby.Lobby

data class LobbyListUiState(
    val lobbies: List<Lobby> = emptyList(),
    val selectedLobby: Lobby? = null,
)
