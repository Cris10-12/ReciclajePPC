package com.udistrital.reciclajeppc.core.util

import com.udistrital.reciclajeppc.domain.model.User
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

/** 90 segundos -> "Hace 1 minuto"; 3 días -> "Hace 3 días". */
fun formatRelativeTime(millis: Long, now: Long = System.currentTimeMillis()): String {
    val minutes = ((now - millis).coerceAtLeast(0)) / 60_000
    val hours = minutes / 60
    val days = hours / 24
    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "Hace " + pluralize(minutes.toInt(), "minuto", "minutos")
        hours < 24 -> "Hace " + pluralize(hours.toInt(), "hora", "horas")
        days < 30 -> "Hace " + pluralize(days.toInt(), "día", "días")
        days < 365 -> "Hace " + pluralize((days / 30).toInt(), "mes", "meses")
        else -> "Hace " + pluralize((days / 365).toInt(), "año", "años")
    }

}

/** "Reciclador y Generador · Chapinero" (sin barrio si no lo tiene). */
fun describeUser(user: User): String {
    val roles = user.roles.joinToString(" y ") { it.label }
    return if (user.neighborhood.isBlank()) roles else "$roles · ${user.neighborhood}"
}