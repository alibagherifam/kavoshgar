package dev.alibagherifam.kavoshgar.demo.lobby.presenter

import dev.alibagherifam.kavoshgar.demo.lobby.model.Lobby

sealed interface LobbyListUiEvent {
    data class LobbySelection(val lobby: Lobby) : LobbyListUiEvent
}
