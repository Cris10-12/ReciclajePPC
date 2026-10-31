package com.udistrital.reciclajeppc.core.session

import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.model.UserRole
import com.udistrital.reciclajeppc.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Guarda el usuario que inició sesión y el rol activo.
 * Las pantallas observan estos valores y se actualizan solas.
 */
class SessionManager(private val authRepository: AuthRepository) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _activeRole = MutableStateFlow(UserRole.GENERATOR)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    suspend fun login(email: String, password: String): Result<User> =
        authRepository.login(email, password).onSuccess { startSession(it) }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        neighborhood: String,
        password: String
    ): Result<User> =
        authRepository.register(name, email, phone, neighborhood, password)
            .onSuccess { startSession(it) }

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