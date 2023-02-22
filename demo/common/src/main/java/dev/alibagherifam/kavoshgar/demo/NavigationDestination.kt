package dev.alibagherifam.kavoshgar.demo

import dev.alibagherifam.kavoshgar.demo.chat.ChatNavigationArgs

sealed class NavigationDestination {
    object LobbyList : NavigationDestination()
    data class Chat(val args: ChatNavigationArgs) : NavigationDestination()
}
