package com.alibagherifam.kavoshgar.demo.desktop.lobby

import com.alibagherifam.kavoshgar.lobby.ServerInformation

data class LobbyListUiState(
    val lobbies: List<ServerInformation> = emptyList(),
    val selectedLobby: ServerInformation? = null
)
