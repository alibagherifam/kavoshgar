package com.alibagherifam.kavoshgar.demo

import com.alibagherifam.kavoshgar.demo.chat.ChatNavigationArgs

sealed class NavigationDestination {
    object LobbyList : NavigationDestination()
    data class Chat(val args: ChatNavigationArgs) : NavigationDestination()
}
