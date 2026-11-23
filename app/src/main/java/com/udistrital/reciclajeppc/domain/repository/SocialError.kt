package com.udistrital.reciclajeppc.domain.repository

/** Errores de reglas sociales. Las pantallas los traducen a texto con AppTexts. */
sealed class SocialError : Exception() {
    object SelfAction : SocialError()      // acción sobre uno mismo
    object AlreadyExists : SocialError()   // ya existe esa relación
    object NotConnected : SocialError()    // exige conexión aceptada
    object InvalidComment : SocialError()  // comentario fuera de largo permitido
    object NotFound : SocialError()        // el registro ya no existe
}