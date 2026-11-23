package com.udistrital.reciclajeppc.core.util

import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.UserRole

fun roleIn(request: RecycleRequest, userId: String): UserRole =
    if (request.generatorId == userId) UserRole.GENERATOR else UserRole.RECYCLER

fun List<Conversation>.forRole(
    requests: List<RecycleRequest>,
    userId: String,
    role: UserRole
): List<Conversation> = filter { conversation ->
    val request = requests.firstOrNull { it.id == conversation.requestId }
    request != null && roleIn(request, userId) == role
}