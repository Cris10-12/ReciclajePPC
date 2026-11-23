package com.udistrital.reciclajeppc.domain.model

enum class DocumentType(
    val label: String,
    val shortLabel: String,
    val numericOnly: Boolean,
    val minLength: Int,
    val maxLength: Int
) {
    CC("Cédula de ciudadanía", "CC", true, 6, 10),
    CE("Cédula de extranjería", "CE", true, 6, 10),
    PASSPORT("Pasaporte", "PA", false, 5, 15),
    OTHER("Otro", "Otro", false, 5, 15)
}