package com.udistrital.reciclajeppc.domain.model

data class RecycleRequest(
    val id: String,
    val generatorId: String,
    val generatorName: String,
    val material: MaterialType,
    val weightKg: Double,
    val address: String,
    val neighborhood: String,
    val availability: String,
    val notes: String,
    val status: RequestStatus,
    val recyclerName: String? = null,
    val extraMaterials: List<MaterialType> = emptyList()
) {
    /** Todos los materiales de la solicitud (el principal va primero). */
    val materials: List<MaterialType>
        get() = (listOf(material) + extraMaterials).distinct()
}