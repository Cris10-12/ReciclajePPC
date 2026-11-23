package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.core.util.isValidRecommendation
import com.udistrital.reciclajeppc.core.util.newId
import com.udistrital.reciclajeppc.domain.model.Connection
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.model.Favorite
import com.udistrital.reciclajeppc.domain.model.Recommendation
import com.udistrital.reciclajeppc.domain.repository.SocialError
import com.udistrital.reciclajeppc.domain.repository.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSocialRepository : SocialRepository {

    private val connections = MutableStateFlow(FakeData.connections)
    private val favorites = MutableStateFlow(FakeData.favorites)
    private val recommendations = MutableStateFlow(FakeData.recommendations)

    // ---------------- Conexiones ----------------

    override fun observeConnections(userId: String): Flow<List<Connection>> =
        connections.map { list -> list.filter { it.involves(userId) } }

    override suspend fun sendRequest(requesterId: String, receiverId: String): Result<Unit> {
        if (requesterId == receiverId) return Result.failure(SocialError.SelfAction)

        val now = System.currentTimeMillis()
        val current = connections.value
        val existing = current.firstOrNull {
            it.involves(requesterId) && it.involves(receiverId)
        }

        connections.value = when {
            existing == null -> current + Connection(
                id = newId("cn"),
                requesterId = requesterId,
                receiverId = receiverId,
                status = ConnectionStatus.PENDING,
                createdAt = now
            )
            // Reenvío tras un rechazo: se reutiliza el mismo registro.
            existing.status == ConnectionStatus.REJECTED -> current.map {
                if (it.id == existing.id) it.copy(
                    requesterId = requesterId,
                    receiverId = receiverId,
                    status = ConnectionStatus.PENDING,
                    createdAt = now,
                    respondedAt = null
                ) else it
            }
            else -> return Result.failure(SocialError.AlreadyExists)
        }
        return Result.success(Unit)
    }

    override suspend fun respond(connectionId: String, accept: Boolean): Result<Unit> {
        val current = connections.value
        val target = current.firstOrNull { it.id == connectionId }
        if (target == null || target.status != ConnectionStatus.PENDING) {
            return Result.failure(SocialError.NotFound)
        }
        val newStatus = if (accept) ConnectionStatus.ACCEPTED else ConnectionStatus.REJECTED
        connections.value = current.map {
            if (it.id == connectionId) {
                it.copy(status = newStatus, respondedAt = System.currentTimeMillis())
            } else it
        }
        return Result.success(Unit)
    }

    override suspend fun removeConnection(connectionId: String): Result<Unit> {
        val current = connections.value
        if (current.none { it.id == connectionId }) {
            return Result.failure(SocialError.NotFound)
        }
        connections.value = current.filterNot { it.id == connectionId }
        return Result.success(Unit)
    }

    // ---------------- Favoritos ----------------

    override fun observeFavorites(userId: String): Flow<List<Favorite>> =
        favorites.map { list -> list.filter { it.userId == userId } }

    override suspend fun toggleFavorite(userId: String, targetId: String): Result<Unit> {
        if (userId == targetId) return Result.failure(SocialError.SelfAction)

        val current = favorites.value
        val existing = current.firstOrNull {
            it.userId == userId && it.favoriteUserId == targetId
        }
        favorites.value = if (existing != null) {
            current.filterNot { it.id == existing.id }
        } else {
            current + Favorite(
                id = newId("fv"),
                userId = userId,
                favoriteUserId = targetId,
                createdAt = System.currentTimeMillis()
            )
        }
        return Result.success(Unit)
    }

    // ---------------- Recomendaciones ----------------

    override fun observeRecommendationsReceived(userId: String): Flow<List<Recommendation>> =
        recommendations.map { list ->
            list.filter { it.recipientId == userId }.sortedByDescending { it.createdAt }
        }

    override suspend fun recommend(
        authorId: String,
        recipientId: String,
        comment: String
    ): Result<Unit> {
        if (authorId == recipientId) return Result.failure(SocialError.SelfAction)

        val connected = connections.value.any {
            it.status == ConnectionStatus.ACCEPTED &&
                    it.involves(authorId) && it.involves(recipientId)
        }
        if (!connected) return Result.failure(SocialError.NotConnected)

        val text = comment.trim()
        if (!isValidRecommendation(text)) return Result.failure(SocialError.InvalidComment)

        val current = recommendations.value
        if (current.any { it.authorId == authorId && it.recipientId == recipientId }) {
            return Result.failure(SocialError.AlreadyExists)
        }

        recommendations.value = current + Recommendation(
            id = newId("rc"),
            authorId = authorId,
            recipientId = recipientId,
            comment = text,
            createdAt = System.currentTimeMillis()
        )
        return Result.success(Unit)
    }

    override suspend fun updateRecommendation(
        recommendationId: String,
        comment: String
    ): Result<Unit> {
        val current = recommendations.value
        if (current.none { it.id == recommendationId }) {
            return Result.failure(SocialError.NotFound)
        }
        val text = comment.trim()
        if (!isValidRecommendation(text)) return Result.failure(SocialError.InvalidComment)

        recommendations.value = current.map {
            if (it.id == recommendationId) it.copy(comment = text) else it
        }
        return Result.success(Unit)
    }

    override suspend fun removeRecommendation(recommendationId: String): Result<Unit> {
        val current = recommendations.value
        if (current.none { it.id == recommendationId }) {
            return Result.failure(SocialError.NotFound)
        }
        recommendations.value = current.filterNot { it.id == recommendationId }
        return Result.success(Unit)
    }
}