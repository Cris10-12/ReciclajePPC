package com.udistrital.reciclajeppc.core.util

import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.domain.model.DocumentType
import com.udistrital.reciclajeppc.domain.model.Recommendation


/** Deja solo los caracteres permitidos para el tipo y respeta el largo máximo. */
fun sanitizeDocument(type: DocumentType, raw: String): String =
    raw.filter { if (type.numericOnly) it.isDigit() else it.isLetterOrDigit() }
        .take(type.maxLength)

/** Devuelve el mensaje de error, o null si el número es válido para ese tipo. */
fun validateDocument(type: DocumentType, number: String): String? {
    if (number.isBlank()) return AppTexts.ERR_DOC_EMPTY
    if (number.length !in type.minLength..type.maxLength) {
        val template = if (type.numericOnly) AppTexts.ERR_DOC_DIGITS else AppTexts.ERR_DOC_ALPHANUM
        return template.format(type.minLength, type.maxLength)
    }
    return null

}

/** Regla única del comentario de recomendación (la usan el ViewModel y el repositorio). */
fun isValidRecommendation(comment: String): Boolean =
    comment.trim().length in Recommendation.MIN_LENGTH..Recommendation.MAX_LENGTH

/** Devuelve el mensaje de error, o null si el comentario es válido. */
fun validateRecommendation(comment: String): String? {
    val length = comment.trim().length
    return when {
        length < Recommendation.MIN_LENGTH ->
            AppTexts.ERR_RECO_TOO_SHORT.format(Recommendation.MIN_LENGTH)
        length > Recommendation.MAX_LENGTH ->
            AppTexts.ERR_RECO_TOO_LONG.format(Recommendation.MAX_LENGTH)
        else -> null
    }
}