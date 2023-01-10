package com.alibagherifam.kavoshgar.chat

data class Message(
    val isMine: Boolean,
    val content: String,
    val timestamp: String
)
