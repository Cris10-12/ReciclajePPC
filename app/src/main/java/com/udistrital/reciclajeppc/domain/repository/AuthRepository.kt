package com.udistrital.reciclajeppc.domain.repository

import com.udistrital.reciclajeppc.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        neighborhood: String,
        password: String
    ): Result<User>

    suspend fun logout()
}