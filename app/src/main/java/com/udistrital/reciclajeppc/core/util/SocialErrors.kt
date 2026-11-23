package com.udistrital.reciclajeppc.core.util

import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.domain.model.Recommendation
import com.udistrital.reciclajeppc.domain.repository.SocialError

/** Traduce un error de las reglas sociales a texto para el usuario. */
fun Throwable.toSocialMessage(): String = when (this) {
    SocialError.SelfAction -> AppTexts.ERR_SOCIAL_SELF
    SocialError.AlreadyExists -> AppTexts.ERR_SOCIAL_ALREADY_EXISTS
    SocialError.NotConnected -> AppTexts.ERR_SOCIAL_NOT_CONNECTED
    SocialError.InvalidComment -> AppTexts.ERR_SOCIAL_INVALID_COMMENT
        .format(Recommendation.MIN_LENGTH, Recommendation.MAX_LENGTH)
    SocialError.NotFound -> AppTexts.ERR_SOCIAL_NOT_FOUND
    else -> AppTexts.ERR_SOCIAL_GENERIC
}