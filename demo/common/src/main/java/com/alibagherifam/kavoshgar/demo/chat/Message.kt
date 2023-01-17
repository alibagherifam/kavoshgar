package com.alibagherifam.kavoshgar.demo.chat

data class Message(
    val isMine: Boolean,
    val content: String,
    val timestamp: String
)
