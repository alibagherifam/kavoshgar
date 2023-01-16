package com.alibagherifam.kavoshgar.demo.lobby

import com.alibagherifam.kavoshgar.lobby.ServerInformation

data class LobbyListUiState(
    val servers: List<ServerInformation> = emptyList(),
    val selectedServer: ServerInformation? = null
)