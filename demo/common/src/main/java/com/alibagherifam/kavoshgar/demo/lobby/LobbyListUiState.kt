package com.alibagherifam.kavoshgar.demo.lobby

import com.alibagherifam.kavoshgar.discovery.ServerInformation

data class LobbyListUiState(
    val servers: List<ServerInformation> = emptyList(),
    val selectedServer: ServerInformation? = null
)
