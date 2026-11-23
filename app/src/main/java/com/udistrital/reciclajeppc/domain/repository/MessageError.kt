package com.udistrital.reciclajeppc.domain.repository

sealed class MessageError : Exception() {
    object NotFound : MessageError()
    object NotParticipant : MessageError()
    object NotAllowed : MessageError()
    object EmptyMessage : MessageError()
}