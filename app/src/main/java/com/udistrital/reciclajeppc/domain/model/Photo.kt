package com.udistrital.reciclajeppc.domain.model

data class Photo(
    val id: String,
    val ownerId: String,
    val uri: String,
    val caption: String = "",
    val createdAt: Long
)