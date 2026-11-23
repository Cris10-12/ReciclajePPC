package com.udistrital.reciclajeppc.domain.repository

import com.udistrital.reciclajeppc.domain.model.Connection
import com.udistrital.reciclajeppc.domain.model.Favorite
import com.udistrital.reciclajeppc.domain.model.Recommendation
import kotlinx.coroutines.flow.Flow

interface SocialRepository {

    // ---- Conexiones ----
    /** Todas las conexiones donde participa el usuario, en cualquier estado. */
    fun observeConnections(userId: String): Flow<List<Connection>>

    suspend fun sendRequest(requesterId: String, receiverId: String): Result<Unit>

    suspend fun respond(connectionId: String, accept: Boolean): Result<Unit>

    /** Cancela una solicitud pendiente o elimina una conexión aceptada. */
    suspend fun removeConnection(connectionId: String): Result<Unit>

    // ---- Favoritos ----
    fun observeFavorites(userId: String): Flow<List<Favorite>>

    /** Marca o desmarca. */
    suspend fun toggleFavorite(userId: String, targetId: String): Result<Unit>

    // ---- Recomendaciones ----
    fun observeRecommendationsReceived(userId: String): Flow<List<Recommendation>>

    suspend fun recommend(authorId: String, recipientId: String, comment: String): Result<Unit>

    suspend fun updateRecommendation(recommendationId: String, comment: String): Result<Unit>

    suspend fun removeRecommendation(recommendationId: String): Result<Unit>
}