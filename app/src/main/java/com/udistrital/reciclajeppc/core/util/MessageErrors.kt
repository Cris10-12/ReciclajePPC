package com.udistrital.reciclajeppc.core.util

import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.domain.repository.MessageError

/** Traduce los errores de mensajería a texto para mostrar en pantalla. */
fun Throwable.chatErrorText(): String = when (this) {
    is MessageError.NotAllowed -> AppTexts.CHAT_ERR_NOT_ALLOWED
    is MessageError.NotParticipant -> AppTexts.CHAT_ERR_NOT_PARTICIPANT
    is MessageError.NotFound -> AppTexts.CHAT_ERR_NOT_FOUND
    else -> AppTexts.CHAT_ERR_GENERIC
}