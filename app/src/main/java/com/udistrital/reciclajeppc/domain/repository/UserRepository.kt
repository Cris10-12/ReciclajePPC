package com.udistrital.reciclajeppc.domain.repository

import com.udistrital.reciclajeppc.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUsers(): Flow<List<User>>

    fun observeUser(id: String): Flow<User?>

    suspend fun updateProfile(
        userId: String,
        name: String,
        phone: String,
        neighborhood: String,
        bio: String,
        avatarUri: String?
    ): Result<User>
}