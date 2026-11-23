package com.udistrital.reciclajeppc.domain.repository

import com.udistrital.reciclajeppc.domain.model.DocumentType
import com.udistrital.reciclajeppc.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>

    /** Falla con [DuplicateDocumentException] si el documento ya está registrado. */
    suspend fun register(
        name: String,
        email: String,
        documentType: DocumentType,
        documentNumber: String,
        phone: String,
        neighborhood: String,
        password: String
    ): Result<User>

    suspend fun logout()
}