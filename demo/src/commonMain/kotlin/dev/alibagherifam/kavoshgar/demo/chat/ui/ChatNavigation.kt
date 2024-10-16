package dev.alibagherifam.kavoshgar.demo.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.alibagherifam.kavoshgar.demo.chat.provideMessengerViewModel
import java.net.InetAddress

@Composable
fun ChatDestination(
    args: ChatNavigationArgs,
    onCloserRequest: () -> Unit
) {
    val viewModel = remember {
        provideMessengerViewModel(
            isLobbyOwner = args.isLobbyOwner,
            lobbyAddress = args.lobbyAddress,
            lobbyName = args.lobbyName
        )
    }

    val uiState by viewModel.uiState.collectAsState()
    ChatUi(
        lobbyName = args.lobbyName,
        uiState = uiState,
        eventSink = viewModel.eventSink,
        onBackPress = onCloserRequest
    )
}

data class ChatNavigationArgs(
    val isLobbyOwner: Boolean,
    val lobbyName: String,
    val lobbyAddress: InetAddress? = null
)