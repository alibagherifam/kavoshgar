package dev.alibagherifam.kavoshgar.demo.lobby

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.alibagherifam.kavoshgar.demo.chat.ChatNavigationArgs
import dev.alibagherifam.kavoshgar.demo.lobby.screen.LobbyListScreen

@Composable
internal fun LobbyListDestination(
    onChatPageRequest: (ChatNavigationArgs) -> Unit
) {
    val lobbyScope = rememberCoroutineScope()
    val viewModel = remember {
        provideLobbyListViewModel(lobbyScope)
    }
    LobbyListScreen(
        viewModel = viewModel,
        onChatPageRequest = onChatPageRequest
    )
}
