package dev.alibagherifam.kavoshgar.messenger

import java.net.Socket

sealed interface MessagingSocketState {
    data object Disconnected : MessagingSocketState

    data class Connected(val socket: Socket) : MessagingSocketState {
        val reader = socket.getInputStream().bufferedReader()
        val writer = socket.getOutputStream().bufferedWriter()
    }
}
