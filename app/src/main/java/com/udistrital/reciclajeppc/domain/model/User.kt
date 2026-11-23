package com.udistrital.reciclajeppc.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val documentType: DocumentType,
    val documentNumber: String,
    val phone: String,
    val neighborhood: String,
    val bio: String = "",
    val avatarUri: String? = null,
    val roles: List<UserRole> = UserRole.entries
)