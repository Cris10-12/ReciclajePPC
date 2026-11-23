package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.core.util.newId
import com.udistrital.reciclajeppc.domain.model.DocumentType
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.model.UserRole
import com.udistrital.reciclajeppc.domain.repository.AuthRepository
import com.udistrital.reciclajeppc.domain.repository.DuplicateDocumentException

class FakeAuthRepository(private val users: FakeUserRepository) : AuthRepository {

    // No valida contraseñas. Si el correo pertenece a una cuenta, entra a ella;
    // con cualquier otro correo entra como Cristian, como hasta ahora.
    override suspend fun login(email: String, password: String): Result<User> {
        val user = users.findByEmail(email)
            ?: users.findById(FakeData.currentUser.id)
            ?: FakeData.currentUser
        return Result.success(user)
    }

    override suspend fun register(
        name: String,
        email: String,
        documentType: DocumentType,
        documentNumber: String,
        phone: String,
        neighborhood: String,
        password: String
    ): Result<User> {
        if (users.documentExists(documentType, documentNumber)) {
            return Result.failure(DuplicateDocumentException())
        }
        val user = User(
            id = newId("u"),
            name = name,
            email = email,
            documentType = documentType,
            documentNumber = documentNumber,
            phone = phone,
            neighborhood = neighborhood,
            roles = UserRole.entries
        )
        users.add(user)
        return Result.success(user)
    }

    override suspend fun logout() { /* nada que limpiar por ahora */ }
}