package com.udistrital.reciclajeppc.core.navigation

import com.udistrital.reciclajeppc.domain.model.UserRole

object Routes {
    // Acceso
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN_MENU = "main_menu"

    // Pestañas del Generador
    const val GENERATOR_HOME = "generator_home"
    const val MY_REQUESTS = "my_requests"

    // Pestañas del Reciclador
    const val RECYCLER_HOME = "recycler_home"
    const val EXPLORE = "explore"
    const val AGENDA = "agenda"

    // Pestañas compartidas
    const val MESSAGES = "messages"
    const val PROFILE = "profile"

    // Pantallas sin barra inferior
    const val CREATE_REQUEST = "create_request"
    const val HISTORY = "history"
    const val REQUEST_DETAIL = "request_detail/{requestId}"
    const val CHAT = "chat/{conversationId}"

    // Rutas con parámetros
    fun requestDetail(id: String) = "request_detail/$id"
    fun chat(id: String) = "chat/$id"

    // Rutas donde se muestra la barra inferior
    val tabRoutes = setOf(
        GENERATOR_HOME, MY_REQUESTS,
        RECYCLER_HOME, EXPLORE, AGENDA,
        MESSAGES, PROFILE
    )

    fun homeFor(role: UserRole): String =
        if (role == UserRole.GENERATOR) GENERATOR_HOME else RECYCLER_HOME
}