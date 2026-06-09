package com.example.reacht_android.model

data class ChatMessage(
    val messageId: Int,
    val userId: Int,
    val chatId: Int,
    val timestamp: String,
    val text: String,
    val userUsername: String
)
