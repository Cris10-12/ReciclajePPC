package com.udistrital.reciclajeppc.domain.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val sentAt: Long
)

data class Participant(
    val userId: String,
    val lastReadAt: Long
)

data class Conversation(
    val id: String,
    val requestId: String,
    val participants: List<Participant>,
    val messages: List<ChatMessage>
) {
    val lastMessage: ChatMessage? get() = messages.lastOrNull()

    fun involves(userId: String): Boolean = participants.any { it.userId == userId }

    fun otherUserId(userId: String): String? =
        participants.firstOrNull { it.userId != userId }?.userId

    fun unreadFor(userId: String): Int {
        val lastRead = participants.firstOrNull { it.userId == userId }?.lastReadAt ?: return 0
        return messages.count { it.senderId != userId && it.sentAt > lastRead }
    }
}