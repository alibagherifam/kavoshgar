package dev.alibagherifam.kavoshgar.demo.lobby.presenter

import dev.alibagherifam.kavoshgar.demo.lobby.model.Lobby

sealed interface LobbyListUiEvent {
    data class LobbyClick(val lobby: Lobby) : LobbyListUiEvent
}
