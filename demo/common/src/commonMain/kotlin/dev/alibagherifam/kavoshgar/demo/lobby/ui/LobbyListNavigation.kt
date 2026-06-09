package dev.alibagherifam.kavoshgar.demo.lobby.ui

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.alibagherifam.kavoshgar.demo.lobby.model.Lobby
import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListPresenter
import dev.alibagherifam.kavoshgar.demo.lobby.provideLobbyListViewModel

fun NavGraphBuilder.lobbyList(
    onCreateLobbyClick: (String) -> Unit,
    onJoinLobbyClick: (Lobby) -> Unit
) {
    composable<LobbyList> {
        val presenter: LobbyListPresenter = viewModel {
            provideLobbyListViewModel()
        }

        val uiState by presenter.uiState.collectAsStateWithLifecycle()
        LobbyListUi(
            uiState = uiState,
            eventSink = presenter.eventSink,
            onCreateLobbyClick = onCreateLobbyClick,
            onJoinLobbyClick = onJoinLobbyClick
        )
    }
}

data object LobbyList
