package dev.alibagherifam.kavoshgar.demo.lobby.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.alibagherifam.kavoshgar.demo.chat.ui.ChatNavigationArgs
import dev.alibagherifam.kavoshgar.demo.lobby.presenter.LobbyListPresenter
import dev.alibagherifam.kavoshgar.demo.lobby.provideLobbyListViewModel

@Composable
fun LobbyListDestination(
    onChatPageRequest: (ChatNavigationArgs) -> Unit
) {
    val presenter: LobbyListPresenter = remember {
        provideLobbyListViewModel()
    }

    val uiState by presenter.uiState.collectAsState()
    LobbyListUi(
        uiState = uiState,
        eventSink = presenter.eventSink,
        onChatPageRequest = onChatPageRequest
    )
}
