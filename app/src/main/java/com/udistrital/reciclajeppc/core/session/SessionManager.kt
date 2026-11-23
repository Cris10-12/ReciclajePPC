package com.udistrital.reciclajeppc.core.session

import com.udistrital.reciclajeppc.domain.model.DocumentType
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.model.UserRole
import com.udistrital.reciclajeppc.domain.repository.AuthRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Guarda el usuario que inició sesión y el rol activo.
 * Las pantallas observan estos valores y se actualizan solas.
 */
class SessionManager(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _activeRole = MutableStateFlow(UserRole.GENERATOR)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    suspend fun login(email: String, password: String): Result<User> =
        authRepository.login(email, password).onSuccess { startSession(it) }

    suspend fun register(
        name: String,
        email: String,
        documentType: DocumentType,
        documentNumber: String,
        phone: String,
        neighborhood: String,
        password: String
    ): Result<User> =
        authRepository.register(
            name, email, documentType, documentNumber, phone, neighborhood, password
        ).onSuccess { startSession(it) }

    /** Guarda los cambios del perfil y refresca el usuario de la sesión. */
    suspend fun updateProfile(
        name: String,
        phone: String,
        neighborhood: String,
        bio: String,
        avatarUri: String?
    ): Result<User> {
        val user = _currentUser.value
            ?: return Result.failure(IllegalStateException("No hay sesión activa"))
        return userRepository.updateProfile(user.id, name, phone, neighborhood, bio, avatarUri)
            .onSuccess { _currentUser.value = it }
    }

    fun setRole(role: UserRole) {
        _activeRole.value = role
    }

    suspend fun logout() {
        authRepository.logout()
        _currentUser.value = null
        _activeRole.value = UserRole.GENERATOR
    }

    private fun startSession(user: User) {
        _currentUser.value = user
        _activeRole.value = UserRole.GENERATOR
    }
}