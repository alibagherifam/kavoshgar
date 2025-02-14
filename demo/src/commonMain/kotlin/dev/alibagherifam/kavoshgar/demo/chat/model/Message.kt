package dev.alibagherifam.kavoshgar.demo.chat.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Message(
    val isMine: Boolean,
    val content: String
) {
    val receiveInstant: Instant = Clock.System.now()
}
