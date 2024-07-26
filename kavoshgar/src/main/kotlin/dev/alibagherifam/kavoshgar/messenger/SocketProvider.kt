package dev.alibagherifam.kavoshgar.messenger

import java.net.Socket

interface SocketProvider {
    fun openSocket(): Socket
}
