package com.udistrital.reciclajeppc.core.constants

object AppTexts {
    const val APP_NAME = "Reciclaje"

    // Acciones comunes
    const val ACCEPT = "Aceptar"
    const val CANCEL = "Cancelar"
    const val SAVE = "Guardar"
    const val BACK = "Volver"
    const val SEE_DETAILS = "Ver detalles"
    const val SEE_HISTORY = "Ver historial"
    const val PUBLISH_REQUEST = "Publicar solicitud"
    const val LOGIN = "Iniciar sesión"
    const val CREATE_ACCOUNT = "Crear cuenta"
    const val HAVE_ACCOUNT = "¿Ya tienes cuenta? Inicia sesión"

    const val TAGLINE = "Conecta residuos reciclables con quienes los recolectan"

    // Estados vacíos
    const val EMPTY_REQUESTS = "Aún no hay solicitudes"
    const val EMPTY_AGENDA = "No tienes recolecciones pendientes"
    const val EMPTY_HISTORY = "Todavía no tienes recolecciones realizadas"
    const val EMPTY_MESSAGES = "No tienes mensajes"

    // ---- Mensajes ----
    const val MESSAGES_TITLE = "Mensajes"
    const val EMPTY_MESSAGES_HINT = "Cuando hables con un generador o reciclador, aparecerá aquí."
    const val CHAT_TITLE = "Chat"
    const val CHAT_HINT = "Escribe un mensaje"
    const val CHAT_SEND = "Enviar"
    const val CHAT_EMPTY = "Aún no hay mensajes"
    const val CHAT_EMPTY_HINT = "Escribe el primero para empezar la conversación."

    // ---- Publicar solicitud ----
    const val CREATE_TITLE = "Publicar solicitud"
    const val FIELD_MATERIAL = "¿Qué materiales tienes? (puedes elegir varios)"
    const val FIELD_WEIGHT = "Peso estimado (kg)"
    const val FIELD_ADDRESS = "Dirección de recolección"
    const val FIELD_NEIGHBORHOOD = "Barrio"
    const val FIELD_AVAILABILITY = "Fecha y horario disponible"
    const val FIELD_NOTES = "Notas para el reciclador (opcional)"
    const val ERR_MATERIAL = "Selecciona al menos un material"
    const val ERR_WEIGHT_EMPTY = "Ingresa el peso estimado"
    const val ERR_WEIGHT_INVALID = "Ingresa un peso válido entre 0.1 y 1000 kg"
    const val ERR_ADDRESS = "Ingresa la dirección"
    const val ERR_NEIGHBORHOOD = "Ingresa el barrio"
    const val ERR_AVAILABILITY = "Indica cuándo puedes entregarlo"
    const val PUBLISHING = "Publicando…"

    // ---- Inicio del Reciclador ----
    const val EXPLORE_REQUESTS = "Explorar solicitudes"


    // ---- Explorar ----
    const val EXPLORE_ALL = "Todos"
    const val EXPLORE_EMPTY = "No hay solicitudes con este filtro"
    const val EXPLORE_EMPTY_HINT = "Prueba con otro material o quita el filtro."


    // ---- Detalle de solicitud ----
    const val DETAIL_TITLE = "Detalle de solicitud"
    const val DETAIL_NOT_FOUND = "No encontramos esta solicitud"
    const val DETAIL_NOT_FOUND_HINT = "Puede que haya sido eliminada."
    const val DETAIL_NO_NOTES = "Sin notas"
    const val ACCEPT_REQUEST = "Aceptar solicitud"
    const val COMPLETE_REQUEST = "Marcar como completada"
    const val CANCEL_REQUEST = "Cancelar solicitud"
    const val CANCEL_DIALOG_TITLE = "¿Cancelar esta solicitud?"
    const val CANCEL_DIALOG_MESSAGE = "Los recicladores ya no podrán verla ni aceptarla."
    const val CANCEL_DIALOG_CONFIRM = "Sí, cancelar"
    const val CANCEL_DIALOG_DISMISS = "No, volver"

    // ---- Mis solicitudes y Agenda ----
    const val MY_REQUESTS_TITLE = "Mis solicitudes"
    const val FILTER_ALL = "Todas"
    const val EMPTY_REQUESTS_HINT = "Publica una solicitud y aparecerá aquí."
    const val EMPTY_REQUESTS_FILTER = "No hay solicitudes con este estado"
    const val AGENDA_TITLE = "Agenda"
    const val EMPTY_AGENDA_HINT = "Explora las solicitudes y acepta una para verla aquí."

    // ---- Historial ----
    const val HISTORY_TITLE = "Historial"
    const val EMPTY_HISTORY_HINT = "Aquí verás las solicitudes completadas o canceladas."
    const val EMPTY_HISTORY_FILTER = "No hay solicitudes con este estado"
    const val EMPTY_HISTORY_FILTER_HINT = "Prueba con otro filtro."
    const val EMPTY_REQUESTS_FILTER_HINT = "Prueba con otro filtro."

    // ---- Perfil ----
    const val PROFILE_TITLE = "Perfil"
    const val PROFILE_EMAIL = "Correo"
    const val PROFILE_PHONE = "Teléfono"
    const val PROFILE_NEIGHBORHOOD = "Barrio"
    const val PROFILE_ACTIVE_ROLE = "Rol activo"
    const val CHANGE_ROLE = "Cambiar de rol"
    const val LOGOUT = "Cerrar sesión"
    const val LOGOUT_DIALOG_TITLE = "¿Cerrar sesión?"
    const val LOGOUT_DIALOG_MESSAGE = "Tendrás que iniciar sesión de nuevo para usar la app."
    const val LOGOUT_DIALOG_CONFIRM = "Sí, salir"
    const val LOGOUT_DIALOG_DISMISS = "Quedarme"


}