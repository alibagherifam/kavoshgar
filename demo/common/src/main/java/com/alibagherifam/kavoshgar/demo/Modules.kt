package com.alibagherifam.kavoshgar.demo

import com.alibagherifam.kavoshgar.chat.ChatRepository
import com.alibagherifam.kavoshgar.chat.ClientChatSocketProvider
import com.alibagherifam.kavoshgar.chat.ServerChatSocketProvider
import com.alibagherifam.kavoshgar.demo.chat.ChatViewModel
import com.alibagherifam.kavoshgar.demo.lobby.LobbyListViewModel
import com.alibagherifam.kavoshgar.lobby.KavoshgarClient
import com.alibagherifam.kavoshgar.lobby.KavoshgarServer
import kotlinx.coroutines.CoroutineScope
import java.net.InetAddress

fun provideLobbyListViewModel(viewModelScope: CoroutineScope) =
    LobbyListViewModel(viewModelScope, provideClient())

fun provideChatViewModel(
    viewModelScope: CoroutineScope,
    isLobbyOwner: Boolean,
    serverAddress: InetAddress?,
    lobbyName: String
) = ChatViewModel(
    viewModelScope,
    chatRepository = provideChatRepository(
        isLobbyOwner,
        serverAddress
    ),
    server = provideServer(
        isLobbyOwner,
        lobbyName
    )
)

fun provideChatRepository(
    isLobbyOwner: Boolean,
    serverAddress: InetAddress?
) = ChatRepository(
    socketProvider = when {
        isLobbyOwner -> ServerChatSocketProvider()
        else -> ClientChatSocketProvider(serverAddress!!)
    }
)

fun provideServer(
    isLobbyOwner: Boolean,
    lobbyName: String
) = when {
    isLobbyOwner -> KavoshgarServer(lobbyName)
    else -> null
}

fun provideClient() = KavoshgarClient()