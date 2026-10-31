package com.udistrital.reciclajeppc.domain.model

data class ChatMessage(
    val id: String,
    val text: String,
    val isMine: Boolean,
    val time: String
)

data class Conversation(
    val id: String,
    val contactName: String,
    val lastMessage: String,
    val time: String,
    val unread: Int,
    val messages: List<ChatMessage>,
    /** Rol que yo tengo en esta conversación (GENERATOR: hablo con un reciclador). */
    val myRole: UserRole = UserRole.GENERATOR
)