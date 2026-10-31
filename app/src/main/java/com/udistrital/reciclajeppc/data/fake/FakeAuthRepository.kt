package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {

    // Acepta cualquier credencial, sin validar nada
    override suspend fun login(email: String, password: String): Result<User> =
        Result.success(FakeData.currentUser)

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        neighborhood: String,
        password: String
    ): Result<User> = Result.success(
        FakeData.currentUser.copy(
            name = name.ifBlank { FakeData.currentUser.name },
            email = email.ifBlank { FakeData.currentUser.email },
            phone = phone.ifBlank { FakeData.currentUser.phone },
            neighborhood = neighborhood.ifBlank { FakeData.currentUser.neighborhood }
        )
    )

    override suspend fun logout() { /* nada que limpiar por ahora */ }
}