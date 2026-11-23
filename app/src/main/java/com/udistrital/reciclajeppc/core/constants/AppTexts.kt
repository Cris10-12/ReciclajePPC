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
    const val CHAT_VIEW_REQUEST = "Ver solicitud"
    const val CHAT_READ_ONLY = "Esta solicitud fue cancelada. Puedes leer la conversación, pero ya no escribir."
    const val MESSAGES_EMPTY = "Aún no tienes conversaciones"
    const val MESSAGES_EMPTY_HINT = "Cuando alguien te escriba por una solicitud, aparecerá aquí"


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

    // ---- Perfil mejorado y edición ----
    const val EDIT_PROFILE = "Editar perfil"
    const val EDIT_PROFILE_TITLE = "Editar perfil"
    const val PROFILE_ABOUT = "Sobre mí"
    const val PROFILE_BIO_EMPTY = "Aún no has escrito una biografía."
    const val PROFILE_ROLES = "Roles"
    const val NOT_SET = "Sin registrar"
    const val STAT_CONNECTIONS = "Conexiones"
    const val STAT_FAVORITES = "Favoritos"
    const val STAT_RECOMMENDATIONS = "Recomendaciones"
    const val STAT_PHOTOS = "Fotos"
    const val FIELD_NAME = "Nombre completo"
    const val FIELD_PHONE = "Teléfono"
    const val FIELD_BIO = "Biografía"
    const val ADD_PHOTO = "Agregar foto"
    const val CHANGE_PHOTO = "Cambiar foto"
    const val REMOVE_PHOTO = "Quitar foto"
    const val SAVE_CHANGES = "Guardar cambios"
    const val SAVING = "Guardando…"
    const val ERR_NAME_EMPTY = "Ingresa tu nombre"
    const val ERR_PHONE_INVALID = "Ingresa un teléfono válido (7 a 15 dígitos)"
    const val ERR_SAVE_PROFILE = "No pudimos guardar los cambios. Intenta de nuevo"
    const val EDIT_LOCKED_NOTE = "El correo y el documento no se pueden modificar."


    // ---- Documento de identificación ----
    const val FIELD_DOCUMENT_TYPE = "Tipo de documento"
    const val FIELD_DOCUMENT_NUMBER = "Número de documento"
    const val ERR_DOC_EMPTY = "Ingresa tu número de documento"
    const val ERR_DOC_DIGITS = "Ingresa entre %1\$d y %2\$d dígitos"
    const val ERR_DOC_ALPHANUM = "Ingresa entre %1\$d y %2\$d letras o números"
    const val ERR_DOC_DUPLICATE = "Ya existe una cuenta con este documento"
    const val ERR_REGISTER_GENERIC = "No pudimos crear la cuenta. Intenta de nuevo"
    const val PROFILE_DOCUMENT = "Documento"


    // ---- Galería ----
    const val MY_GALLERY = "Mi galería de fotos"
    const val GALLERY_TITLE_OWN = "Mis fotos"
    const val GALLERY_TITLE_OTHER = "Fotos de %1\$s"
    const val GALLERY_TITLE_FALLBACK = "Fotos"
    const val ADD_PHOTOS = "Agregar fotos"
    const val GALLERY_EMPTY_OWN = "Aún no tienes fotos"
    const val GALLERY_EMPTY_OWN_HINT =
        "Sube fotos de tu material o de tus recolecciones para que otros te conozcan."
    const val GALLERY_EMPTY_OTHER = "Esta persona aún no tiene fotos"
    const val GALLERY_EMPTY_OTHER_HINT = "Vuelve más tarde."
    const val GALLERY_LIMIT = "Máximo %1\$d fotos"
    const val GALLERY_LIMIT_REACHED = "Llegaste al máximo de fotos. Algunas no se agregaron."
    const val DELETE_PHOTO = "Eliminar foto"
    const val DELETE_PHOTO_TITLE = "¿Eliminar esta foto?"
    const val DELETE_PHOTO_MESSAGE = "Se quitará de tu galería."
    const val DELETE_PHOTO_CONFIRM = "Sí, eliminar"
    const val DELETE_PHOTO_DISMISS = "No, conservar"
    const val CLOSE = "Cerrar"


    // ---- Perfil público ----
    const val SEE_ALL = "Ver todas"
    const val UNKNOWN_USER = "Usuario"
    const val PUBLIC_NOT_FOUND = "No encontramos este perfil"
    const val PUBLIC_NOT_FOUND_HINT = "Puede que la cuenta ya no exista."
    const val PUBLIC_PHOTOS = "Fotos"
    const val PUBLIC_NO_PHOTOS = "Aún no tiene fotos."
    const val PUBLIC_RECOMMENDATIONS = "Recomendaciones"
    const val PUBLIC_NO_RECOMMENDATIONS = "Aún no tiene recomendaciones."
    const val CONNECT = "Conectar"
    const val CONNECT_RECEIVED = "Te envió una solicitud de conexión"
    const val CONNECTED = "Conectados"
    const val ADD_FAVORITE = "Agregar a favoritos"
    const val REMOVE_FAVORITE = "Quitar de favoritos"


    // ---- Perfil: enlaces destacados ----
    const val VIEW_PROFILE = "Ver perfil"
    const val ROLE_GENERATOR = "Generador"
    const val ROLE_RECYCLER = "Reciclador"

    // ---- Conexiones ----
    const val CONNECTIONS_TITLE = "Mis conexiones"
    const val MY_CONNECTIONS = "Mis conexiones"
    const val TAB_CONNECTED = "Conexiones"
    const val TAB_RECEIVED = "Recibidas"
    const val TAB_SENT = "Enviadas"
    const val EMPTY_CONNECTED = "Aún no tienes conexiones"
    const val EMPTY_CONNECTED_HINT = "Conecta con recicladores y generadores desde su perfil."
    const val EMPTY_RECEIVED = "No tienes solicitudes pendientes"
    const val EMPTY_RECEIVED_HINT = "Cuando alguien quiera conectar contigo, aparecerá aquí."
    const val EMPTY_SENT = "No has enviado solicitudes"
    const val EMPTY_SENT_HINT = "Las solicitudes que envíes aparecerán aquí hasta que respondan."
    const val SENT_PREFIX = "Enviada"
    const val CONNECT_SENT_INFO = "Solicitud enviada. Esperando respuesta."
    const val CANCEL_CONNECT_REQUEST = "Cancelar solicitud de conexión"
    const val REJECT = "Rechazar"
    const val REMOVE_CONNECTION = "Eliminar conexión"
    const val REMOVE_CONNECTION_TITLE = "¿Eliminar conexión?"
    const val REMOVE_CONNECTION_MESSAGE =
        "Dejarás de estar conectado con %1\$s. Podrás enviar una nueva solicitud más adelante."
    const val REMOVE_CONNECTION_CONFIRM = "Sí, eliminar"


    // ---- Conexiones de otra persona ----
    const val USER_CONNECTIONS_TITLE = "Conexiones de %1\$s"
    const val EMPTY_USER_CONNECTIONS = "Aún no tiene conexiones"
    const val EMPTY_USER_CONNECTIONS_HINT = "Cuando se conecte con alguien, aparecerá aquí."

    // ---- Favoritos ----
    const val FAVORITES_TITLE = "Mis favoritos"
    const val EMPTY_FAVORITES = "Aún no tienes favoritos"
    const val EMPTY_FAVORITES_HINT =
        "Marca con el corazón a las personas que quieras encontrar rápido."

    // ---- Recomendaciones ----
    const val RECOMMEND = "Recomendar"
    const val RECOMMEND_EDIT = "Editar"
    const val RECOMMEND_TITLE = "Nueva recomendación"
    const val RECOMMEND_EDIT_TITLE = "Editar recomendación"
    const val RECOMMEND_INTRO =
        "Cuéntales a otros por qué recomiendas a esta persona. Se mostrará en su perfil."
    const val RECOMMEND_FIELD = "Tu recomendación"
    const val RECOMMEND_COUNTER = "%1\$d/%2\$d"
    const val RECOMMEND_SEND = "Publicar recomendación"
    const val RECOMMEND_REMOVE = "Retirar recomendación"
    const val RECOMMEND_REMOVE_TITLE = "¿Retirar tu recomendación?"
    const val RECOMMEND_REMOVE_MESSAGE = "Se quitará del perfil de %1\$s."
    const val RECOMMEND_REMOVE_CONFIRM = "Sí, retirar"
    const val RECOMMEND_SELF = "No puedes recomendarte a ti mismo"
    const val RECOMMEND_SELF_HINT = "Las recomendaciones las escriben otras personas sobre ti."
    const val RECOMMEND_NOT_CONNECTED = "Primero conecta con esta persona"
    const val RECOMMEND_NOT_CONNECTED_HINT =
        "Solo puedes recomendar a personas con las que ya estás conectado."
    const val ERR_RECO_TOO_SHORT = "Escribe al menos %1\$d caracteres"
    const val ERR_RECO_TOO_LONG = "Máximo %1\$d caracteres"

    // ---- Errores sociales (traducción de SocialError) ----
    const val ERR_SOCIAL_SELF = "No puedes hacer esto contigo mismo"
    const val ERR_SOCIAL_ALREADY_EXISTS = "Esta acción ya fue realizada"
    const val ERR_SOCIAL_NOT_CONNECTED = "Necesitas estar conectado con esta persona"
    const val ERR_SOCIAL_INVALID_COMMENT = "El comentario debe tener entre %1\$d y %2\$d caracteres"
    const val ERR_SOCIAL_NOT_FOUND = "Este registro ya no existe"
    const val ERR_SOCIAL_GENERIC = "No pudimos completar la acción. Intenta de nuevo"

    // ---- Personas ----
    const val PEOPLE_TITLE = "Personas"
    const val PEOPLE_SEARCH_HINT = "Buscar por nombre o barrio"
    const val PEOPLE_FILTER_ALL = "Todos"
    const val PEOPLE_FILTER_GENERATORS = "Generadores"
    const val PEOPLE_FILTER_RECYCLERS = "Recicladores"
    const val PEOPLE_SHORTCUT_REQUESTS = "Solicitudes"
    const val PEOPLE_EMPTY = "No encontramos personas"
    const val PEOPLE_EMPTY_HINT = "Prueba con otro nombre, barrio o filtro."
    const val BADGE_OVERFLOW = "99+"


    // ---- Abrir chat desde solicitud y perfil ----
    const val OPEN_CHAT = "Abrir chat"
    const val SEND_MESSAGE = "Enviar mensaje"
    const val CHAT_HINT_WAIT = "El chat se habilita cuando un reciclador acepte tu solicitud."
    const val CHAT_PROFILE_HINT =
        "Para chatear con esta persona deben compartir una solicitud aceptada."
    const val CHAT_ERR_NOT_ALLOWED = "Todavía no se puede abrir el chat de esta solicitud."
    const val CHAT_ERR_NOT_PARTICIPANT = "Solo quienes participan en la solicitud pueden chatear."
    const val CHAT_ERR_NOT_FOUND = "No encontramos la solicitud o el chat."
    const val CHAT_ERR_GENERIC = "No pudimos abrir el chat. Intenta de nuevo."



    const val CLOSE_TRAIL = "Volver al punto de partida"

}