package dev.alibagherifam.kavoshgar.demo.chat.ui

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.alibagherifam.kavoshgar.demo.chat.presenter.ChatPresenter
import dev.alibagherifam.kavoshgar.demo.chat.provideMessengerViewModel
import java.net.InetAddress

fun NavGraphBuilder.chat(
    onCloseLobby: () -> Unit
) {
    composable<Chat> { backStackEntry ->
        val args = backStackEntry.toRoute<Chat>()
        val presenter: ChatPresenter = viewModel {
            provideMessengerViewModel(
                isLobbyOwner = args.isLobbyOwner,
                lobbyAddress = args.lobbyAddress,
                lobbyName = args.lobbyName
            )
        }

        val uiState by presenter.uiState.collectAsStateWithLifecycle()
        ChatUi(
            lobbyName = args.lobbyName,
            uiState = uiState,
            eventSink = presenter.eventSink,
            onBackPress = onCloseLobby
        )
    }
}

data class Chat(
    val isLobbyOwner: Boolean,
    val lobbyName: String,
    val lobbyAddress: InetAddress? = null
)
