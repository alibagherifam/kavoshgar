package com.alibagherifam.kavoshgar.chat

import java.net.Socket

interface ChatSocketProvider {
    fun openSocket(): Socket
}
