package dev.alibagherifam.kavoshgar.demo.chat

import dev.alibagherifam.kavoshgar.demo.chat.presenter.MessengerViewModel
import dev.alibagherifam.kavoshgar.discovery.KavoshgarServer
import dev.alibagherifam.kavoshgar.messenger.ClientSocketProvider
import dev.alibagherifam.kavoshgar.messenger.MessengerService
import dev.alibagherifam.kavoshgar.messenger.ServerSocketProvider
import kotlinx.coroutines.CoroutineScope
import java.net.InetAddress

internal fun provideMessengerViewModel(
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

private fun provideMessengerService(
    isLobbyOwner: Boolean,
    serverAddress: InetAddress?
) = MessengerService(
    socketProvider = when {
        isLobbyOwner -> ServerSocketProvider()
        else -> ClientSocketProvider(serverAddress!!)
    }
)

private fun provideServer(
    isLobbyOwner: Boolean,
    lobbyName: String
) = when {
    isLobbyOwner -> KavoshgarServer(lobbyName)
    else -> null
}
