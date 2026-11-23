package com.udistrital.reciclajeppc.domain.repository

import com.udistrital.reciclajeppc.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun observeConversations(userId: String): Flow<List<Conversation>>
    fun observeConversation(id: String): Flow<Conversation?>

    suspend fun getOrCreateConversation(requestId: String, userId: String): Result<String>

    suspend fun sendMessage(conversationId: String, senderId: String, text: String): Result<Unit>

    suspend fun markAsRead(conversationId: String, userId: String)
}