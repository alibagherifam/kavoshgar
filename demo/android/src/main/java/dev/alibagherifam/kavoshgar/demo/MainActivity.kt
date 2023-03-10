package dev.alibagherifam.kavoshgar.demo

import android.os.Bundle
import android.util.Log as AndroidLog
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.alibagherifam.kavoshgar.demo.chat.ChatNavigationArgs
import dev.alibagherifam.kavoshgar.demo.chat.ChatScreen
import dev.alibagherifam.kavoshgar.demo.lobby.LobbyListScreen
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme
import dev.alibagherifam.kavoshgar.demo.NavigationDestination
import dev.alibagherifam.kavoshgar.demo.provideLobbyListViewModel
import dev.alibagherifam.kavoshgar.demo.provideMessengerViewModel
import dev.alibagherifam.kavoshgar.logger.Log
import dev.alibagherifam.kavoshgar.logger.LogPriority
import dev.alibagherifam.kavoshgar.logger.Logger

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.install(getLogger())
        setContent {
            AppTheme {
                NavHost()
            }
        }
    }
}

@Composable
fun NavHost() {
    var currentDestination: NavigationDestination by remember {
        mutableStateOf(NavigationDestination.LobbyList)
    }
    when (currentDestination) {
        NavigationDestination.LobbyList -> LobbyListDestination(
            onChatPageRequest = { args ->
                currentDestination = NavigationDestination.Chat(args)
            }
        )
        is NavigationDestination.Chat -> ChatDestination(
            args = (currentDestination as NavigationDestination.Chat).args,
            onCloserRequest = {
                currentDestination = NavigationDestination.LobbyList
            }
        )
    }
}

@Composable
fun ChatDestination(
    args: ChatNavigationArgs,
    onCloserRequest: () -> Unit
) {
    val chatScope = rememberCoroutineScope()
    val viewModel = remember {
        provideMessengerViewModel(
            viewModelScope = chatScope,
            isLobbyOwner = args.isLobbyOwner,
            lobbyAddress = args.lobbyAddress,
            lobbyName = args.lobbyName
        )
    }
    ChatScreen(
        lobbyName = args.lobbyName,
        viewModel = viewModel,
        onCloseRequest = onCloserRequest
    )
}

@Composable
fun LobbyListDestination(
    onChatPageRequest: (ChatNavigationArgs) -> Unit
) {
    val lobbyScope = rememberCoroutineScope()
    val viewModel = remember {
        provideLobbyListViewModel(lobbyScope)
    }
    LobbyListScreen(viewModel, onChatPageRequest)
}

private fun getLogger() = Logger { priority, tag, message, error ->
    when (priority) {
        LogPriority.INFO -> AndroidLog.i(tag, message!!)
        LogPriority.ERROR -> AndroidLog.e(tag, message, error)
    }
}
