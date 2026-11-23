package com.udistrital.reciclajeppc.domain.model

enum class ConnectionStatus { PENDING, ACCEPTED, REJECTED }

/** Solicitud de conexión entre dos usuarios. Un solo registro por pareja. */
data class Connection(
    val id: String,
    val requesterId: String,
    val receiverId: String,
    val status: ConnectionStatus,
    val createdAt: Long,
    val respondedAt: Long? = null
) {
    fun involves(userId: String): Boolean = requesterId == userId || receiverId == userId

    fun otherUserId(userId: String): String =
        if (requesterId == userId) receiverId else requesterId
}

/** Favorito unilateral: [userId] marca a [favoriteUserId]. */
data class Favorite(
    val id: String,
    val userId: String,
    val favoriteUserId: String,
    val createdAt: Long
)

/** Recomendación de [authorId] hacia [recipientId]. Una por pareja. */
data class Recommendation(
    val id: String,
    val authorId: String,
    val recipientId: String,
    val comment: String,
    val createdAt: Long
) {
    companion object {
        const val MIN_LENGTH = 10
        const val MAX_LENGTH = 200
    }
}