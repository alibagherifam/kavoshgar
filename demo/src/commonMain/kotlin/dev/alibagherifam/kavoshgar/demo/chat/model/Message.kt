package dev.alibagherifam.kavoshgar.demo.chat.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Message(
    val isMine: Boolean,
    val content: String
) {
    private val createdDate = Date(System.currentTimeMillis())

    val timestamp: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(createdDate)
}
