package com.udistrital.reciclajeppc.domain.model

enum class RequestStatus(val label: String) {
    AVAILABLE("Disponible"),
    ACCEPTED("Aceptada"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada")
}