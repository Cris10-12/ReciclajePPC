package com.udistrital.reciclajeppc.core.navigation

import com.udistrital.reciclajeppc.domain.model.UserRole

object Routes {
    // Acceso
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN_MENU = "main_menu"

    const val NODES = "nodes"
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
    const val PEOPLE = "people"


    // Pantallas sin barra inferior
    const val CREATE_REQUEST = "create_request"
    const val HISTORY = "history"
    const val EDIT_PROFILE = "edit_profile"
    const val GALLERY = "gallery/{userId}"
    const val REQUEST_DETAIL = "request_detail/{requestId}"
    const val CHAT = "chat/{conversationId}"
    const val PUBLIC_PROFILE = "user/{userId}"
    const val CONNECTIONS = "connections?tab={tab}"
    const val ARG_TAB = "tab"
    const val TAB_RECEIVED = "received"
    const val FAVORITES = "favorites"
    const val USER_CONNECTIONS = "user_connections/{userId}"
    const val RECOMMEND = "recommend/{userId}"


    // Rutas con parámetros
    fun requestDetail(id: String) = "request_detail/$id"
    fun chat(id: String) = "chat/$id"
    fun gallery(userId: String) = "gallery/$userId"
    fun publicProfile(userId: String) = "user/$userId"
    fun userConnections(userId: String) = "user_connections/$userId"
    fun recommend(userId: String) = "recommend/$userId"
    fun connections(tab: String? = null) =
        if (tab == null) "connections" else "connections?tab=$tab"

    // Rutas donde se muestra la barra inferior
    val tabRoutes = setOf(
        GENERATOR_HOME, MY_REQUESTS,
        RECYCLER_HOME, EXPLORE, AGENDA,
        PEOPLE, PROFILE
    )

    fun homeFor(role: UserRole): String =
        if (role == UserRole.GENERATOR) GENERATOR_HOME else RECYCLER_HOME
}