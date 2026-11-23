package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.domain.model.DocumentType
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.SocialError
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeUserRepository : UserRepository {

    private val users = MutableStateFlow(FakeData.users)

    override fun observeUsers(): Flow<List<User>> = users.asStateFlow()

    override fun observeUser(id: String): Flow<User?> =
        users.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun updateProfile(
        userId: String,
        name: String,
        phone: String,
        neighborhood: String,
        bio: String,
        avatarUri: String?
    ): Result<User> {
        val current = users.value.firstOrNull { it.id == userId }
            ?: return Result.failure(SocialError.NotFound)
        val updated = current.copy(
            name = name,
            phone = phone,
            neighborhood = neighborhood,
            bio = bio,
            avatarUri = avatarUri
        )
        users.update { list -> list.map { if (it.id == userId) updated else it } }
        return Result.success(updated)
    }

    // ---- Solo para FakeAuthRepository (el registro real lo hará Firebase Auth) ----

    fun add(user: User) {
        users.update { it + user }
    }

    fun findById(id: String): User? = users.value.firstOrNull { it.id == id }

    fun findByEmail(email: String): User? =
        users.value.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }

    fun documentExists(type: DocumentType, number: String): Boolean =
        users.value.any { it.documentType == type && it.documentNumber == number }
}