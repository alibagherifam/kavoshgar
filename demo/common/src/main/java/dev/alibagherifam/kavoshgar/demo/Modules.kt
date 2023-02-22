package dev.alibagherifam.kavoshgar.demo

import dev.alibagherifam.kavoshgar.messenger.MessengerService
import dev.alibagherifam.kavoshgar.messenger.ClientSocketProvider
import dev.alibagherifam.kavoshgar.messenger.ServerSocketProvider
import dev.alibagherifam.kavoshgar.demo.chat.MessengerViewModel
import dev.alibagherifam.kavoshgar.demo.lobby.LobbyListViewModel
import dev.alibagherifam.kavoshgar.discovery.KavoshgarClient
import dev.alibagherifam.kavoshgar.discovery.KavoshgarServer
import kotlinx.coroutines.CoroutineScope
import java.net.InetAddress

fun provideLobbyListViewModel(viewModelScope: CoroutineScope) =
    LobbyListViewModel(viewModelScope, provideClient())

fun provideMessengerViewModel(
    viewModelScope: CoroutineScope,
    isLobbyOwner: Boolean,
    lobbyAddress: InetAddress?,
    lobbyName: String
) = MessengerViewModel(
    viewModelScope,
    messenger = provideMessengerService(
        isLobbyOwner,
        lobbyAddress
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
