package com.alibagherifam.kavoshgar.demo.desktop

import com.alibagherifam.kavoshgar.demo.desktop.chat.ChatNavigationArgs

sealed class NavigationDestination {
    object LobbyList : NavigationDestination()
    data class Chat(val args: ChatNavigationArgs) : NavigationDestination()
}
