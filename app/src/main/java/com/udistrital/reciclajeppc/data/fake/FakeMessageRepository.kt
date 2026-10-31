package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.domain.model.ChatMessage
import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeMessageRepository : MessageRepository {

    private val conversations = MutableStateFlow(FakeData.conversations)

    override fun observeConversations(): Flow<List<Conversation>> = conversations.asStateFlow()

    override fun observeConversation(id: String): Flow<Conversation?> =
        conversations.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun sendMessage(conversationId: String, text: String) {
        val message = ChatMessage(
            id = "m${System.currentTimeMillis()}",
            text = text,
            isMine = true,
            time = "Ahora"
        )
        conversations.update { list ->
            list.map {
                if (it.id == conversationId) {
                    it.copy(
                        lastMessage = text,
                        time = "Ahora",
                        messages = it.messages + message
                    )
                } else it
            }
        }
    }

    override suspend fun markAsRead(conversationId: String) {
        conversations.update { list ->
            list.map {
                if (it.id == conversationId && it.unread > 0) it.copy(unread = 0) else it
            }
        }
    }
}