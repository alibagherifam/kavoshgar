package com.alibagherifam.kavoshgar.demo

import com.alibagherifam.kavoshgar.chat.ChatRepository
import com.alibagherifam.kavoshgar.chat.ClientChatSocketProvider
import com.alibagherifam.kavoshgar.chat.ServerChatSocketProvider
import com.alibagherifam.kavoshgar.demo.chat.ChatViewModel
import com.alibagherifam.kavoshgar.demo.lobby.LobbyListViewModel
import com.alibagherifam.kavoshgar.lobby.DiscoveryReplyRepository
import com.alibagherifam.kavoshgar.lobby.LobbyDiscoveryRepository
import kotlinx.coroutines.CoroutineScope

fun provideLobbyListViewModel(viewModelScope: CoroutineScope) =
    LobbyListViewModel(viewModelScope, LobbyDiscoveryRepository())

fun provideChatViewModel(
    viewModelScope: CoroutineScope,
    isLobbyOwner: Boolean,
    lobbyAddress: String?,
    lobbyName: String
) = ChatViewModel(
    viewModelScope,
    chatRepository = provideChatRepository(
        isLobbyOwner,
        lobbyAddress
    ),
    discoveryReplyRepository = provideDiscoveryReplyRepository(
        isLobbyOwner,
        lobbyName
    )
)

fun provideChatRepository(
    isLobbyOwner: Boolean,
    lobbyAddress: String?
) = ChatRepository(
    socketProvider = when {
        isLobbyOwner -> ServerChatSocketProvider()
        else -> ClientChatSocketProvider(lobbyAddress!!)
    }
)

fun provideDiscoveryReplyRepository(
    isLobbyOwner: Boolean,
    lobbyName: String
) = when {
    isLobbyOwner -> DiscoveryReplyRepository(lobbyName)
    else -> null
}
