package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.core.util.newId
import com.udistrital.reciclajeppc.domain.model.ChatMessage
import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.model.Participant
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.repository.MessageError
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeMessageRepository(
    private val requestRepository: RequestRepository
) : MessageRepository {

    private val conversations = MutableStateFlow(FakeData.conversations)

    override fun observeConversations(userId: String): Flow<List<Conversation>> =
        conversations.map { list -> list.filter { it.involves(userId) } }

    override fun observeConversation(id: String): Flow<Conversation?> =
        conversations.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun getOrCreateConversation(
        requestId: String,
        userId: String
    ): Result<String> {
        val request = requestRepository.observeRequest(requestId).first()
            ?: return Result.failure(MessageError.NotFound)
        val recyclerId = request.recyclerId

        val isParticipant = userId == request.generatorId ||
                (recyclerId != null && userId == recyclerId)
        if (!isParticipant) return Result.failure(MessageError.NotParticipant)

        val existing = conversations.value.firstOrNull { it.requestId == requestId }
        if (existing != null) return Result.success(existing.id)

        val canStart = request.status == RequestStatus.ACCEPTED ||
                request.status == RequestStatus.COMPLETED
        if (recyclerId == null || !canStart) return Result.failure(MessageError.NotAllowed)

        val now = System.currentTimeMillis()
        val created = Conversation(
            id = newId("c"),
            requestId = requestId,
            participants = listOf(
                Participant(request.generatorId, now),
                Participant(recyclerId, now)
            ),
            messages = emptyList()
        )
        conversations.update { list ->
            if (list.any { it.requestId == requestId }) list else list + created
        }
        return Result.success(conversations.value.first { it.requestId == requestId }.id)
    }

    override suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        text: String
    ): Result<Unit> {
        val conversation = conversations.value.firstOrNull { it.id == conversationId }
            ?: return Result.failure(MessageError.NotFound)
        if (!conversation.involves(senderId)) return Result.failure(MessageError.NotParticipant)

        val request = requestRepository.observeRequest(conversation.requestId).first()
        if (request == null || request.status == RequestStatus.CANCELLED) {
            return Result.failure(MessageError.NotAllowed)
        }

        val body = text.trim()
        if (body.isEmpty()) return Result.failure(MessageError.EmptyMessage)

        val now = System.currentTimeMillis()
        val message = ChatMessage(id = newId("m"), senderId = senderId, text = body, sentAt = now)
        conversations.update { list ->
            list.map { current ->
                if (current.id == conversationId) {
                    current.copy(
                        messages = current.messages + message,
                        participants = current.participants.map { p ->
                            if (p.userId == senderId) p.copy(lastReadAt = now) else p
                        }
                    )
                } else current
            }
        }
        return Result.success(Unit)
    }

    override suspend fun markAsRead(conversationId: String, userId: String) {
        val now = System.currentTimeMillis()
        conversations.update { list ->
            list.map { current ->
                if (current.id == conversationId && current.unreadFor(userId) > 0) {
                    current.copy(
                        participants = current.participants.map { p ->
                            if (p.userId == userId) p.copy(lastReadAt = now) else p
                        }
                    )
                } else current
            }
        }
    }
}