package dev.alibagherifam.kavoshgar.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.alibagherifam.kavoshgar.demo.chat.ChatDestination
import dev.alibagherifam.kavoshgar.demo.chat.ChatNavigationArgs
import dev.alibagherifam.kavoshgar.demo.lobby.LobbyListDestination

sealed class NavigationDestination {
    data object LobbyList : NavigationDestination()
    data class Chat(val args: ChatNavigationArgs) : NavigationDestination()
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
