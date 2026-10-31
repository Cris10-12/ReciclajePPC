package com.udistrital.reciclajeppc.core.util

import java.util.Locale

/** 25.0 -> "25 kg"  |  8.5 -> "8,5 kg" */
fun formatWeight(kg: Double): String =
    if (kg % 1.0 == 0.0) {
        "${kg.toInt()} kg"
    } else {
        "${String.format(Locale("es", "CO"), "%.1f", kg)} kg"
    }

/** "Cristian Ramírez" -> "CR"  (para avatares) */
fun getInitials(name: String): String =
    name.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

/** 1 -> "1 solicitud"  |  3 -> "3 solicitudes" */
fun pluralize(count: Int, singular: String, plural: String): String =
    "$count ${if (count == 1) singular else plural}"