package dev.alibagherifam.kavoshgar.demo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.alibagherifam.kavoshgar.demo.chat.ui.Chat
import dev.alibagherifam.kavoshgar.demo.chat.ui.chat
import dev.alibagherifam.kavoshgar.demo.lobby.ui.LobbyList
import dev.alibagherifam.kavoshgar.demo.lobby.ui.lobbyList

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LobbyList,
    ) {
        lobbyList(
            onCreateLobbyClick = { lobbyName ->
                val args = Chat(
                    isLobbyOwner = true,
                    lobbyName = lobbyName
                )
                navController.navigate(chat)
            },
            onJoinLobbyClick = { lobby ->
                val args = Chat(
                    isLobbyOwner = false,
                    lobbyName = selectedLobby.name,
                    lobbyAddress = selectedLobby.address
                )
                navController.navigate(chat)
            }
        )

        chat(
            onCloseLobby = {
                navController.navigate(LobbyList)
            }
        )
    }
}
