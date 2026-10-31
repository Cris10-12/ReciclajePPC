package com.udistrital.reciclajeppc.domain.repository

import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    fun observeRequests(): Flow<List<RecycleRequest>>
    fun observeRequest(id: String): Flow<RecycleRequest?>

    suspend fun createRequest(request: RecycleRequest)
    suspend fun acceptRequest(id: String, recyclerName: String)
    suspend fun completeRequest(id: String)
    suspend fun cancelRequest(id: String)
}