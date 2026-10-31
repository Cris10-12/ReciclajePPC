package com.udistrital.reciclajeppc.domain.repository

import com.udistrital.reciclajeppc.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun observeConversations(): Flow<List<Conversation>>
    fun observeConversation(id: String): Flow<Conversation?>

    suspend fun sendMessage(conversationId: String, text: String)

    suspend fun markAsRead(conversationId: String)
}