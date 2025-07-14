package com.example.skinvision.data

data class ChatMessage(
    val id: String,
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val messageType: MessageType = MessageType.TEXT
)

enum class MessageType {
    TEXT, IMAGE, QUICK_ACTION
}

data class QuickAction(
    val icon: String,
    val text: String,
    val action: String
)
