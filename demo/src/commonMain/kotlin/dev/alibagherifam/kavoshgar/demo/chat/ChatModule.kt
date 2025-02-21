package dev.alibagherifam.kavoshgar.demo.chat

import dev.alibagherifam.kavoshgar.demo.chat.presenter.ChatPresenter
import dev.alibagherifam.kavoshgar.discovery.KavoshgarServer
import dev.alibagherifam.kavoshgar.messenger.ClientSocketProvider
import dev.alibagherifam.kavoshgar.messenger.MessengerService
import dev.alibagherifam.kavoshgar.messenger.ServerSocketProvider
import java.net.InetAddress

internal fun provideMessengerViewModel(
    isLobbyOwner: Boolean,
    lobbyAddress: InetAddress?,
    lobbyName: String
) = ChatPresenter(
    lobbyName = lobbyName,
    messenger = provideMessengerService(
        isLobbyOwner,
        lobbyAddress
    ),
    server = provideServer(isLobbyOwner)
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

private fun provideServer(isLobbyOwner: Boolean) =
    when {
        isLobbyOwner -> KavoshgarServer()
        else -> null
    }
