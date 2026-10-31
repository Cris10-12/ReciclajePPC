package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeRequestRepository : RequestRepository {

    private val requests = MutableStateFlow(FakeData.requests)

    override fun observeRequests(): Flow<List<RecycleRequest>> = requests.asStateFlow()

    override fun observeRequest(id: String): Flow<RecycleRequest?> =
        requests.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun createRequest(request: RecycleRequest) {
        requests.update { listOf(request) + it }
    }

    override suspend fun acceptRequest(id: String, recyclerName: String) {
        updateRequest(id) { it.copy(status = RequestStatus.ACCEPTED, recyclerName = recyclerName) }
    }

    override suspend fun completeRequest(id: String) {
        updateRequest(id) { it.copy(status = RequestStatus.COMPLETED) }
    }

    override suspend fun cancelRequest(id: String) {
        updateRequest(id) { it.copy(status = RequestStatus.CANCELLED) }
    }

    private fun updateRequest(id: String, change: (RecycleRequest) -> RecycleRequest) {
        requests.update { list -> list.map { if (it.id == id) change(it) else it } }
    }
}