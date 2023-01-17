package com.alibagherifam.kavoshgar.demo

import com.alibagherifam.kavoshgar.messenger.MessengerService
import com.alibagherifam.kavoshgar.messenger.ClientSocketProvider
import com.alibagherifam.kavoshgar.messenger.ServerSocketProvider
import com.alibagherifam.kavoshgar.demo.chat.MessengerViewModel
import com.alibagherifam.kavoshgar.demo.lobby.LobbyListViewModel
import com.alibagherifam.kavoshgar.discovery.KavoshgarClient
import com.alibagherifam.kavoshgar.discovery.KavoshgarServer
import kotlinx.coroutines.CoroutineScope
import java.net.InetAddress

fun provideLobbyListViewModel(viewModelScope: CoroutineScope) =
    LobbyListViewModel(viewModelScope, provideClient())

fun provideMessengerViewModel(
    viewModelScope: CoroutineScope,
    isLobbyOwner: Boolean,
    serverAddress: InetAddress?,
    lobbyName: String
) = MessengerViewModel(
    viewModelScope,
    messenger = provideMessengerService(
        isLobbyOwner,
        serverAddress
    ),
    server = provideServer(
        isLobbyOwner,
        lobbyName
    )
)

fun provideMessengerService(
    isLobbyOwner: Boolean,
    serverAddress: InetAddress?
) = MessengerService(
    socketProvider = when {
        isLobbyOwner -> ServerSocketProvider()
        else -> ClientSocketProvider(serverAddress!!)
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
