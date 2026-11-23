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

    // Solo se acepta si sigue disponible (evita que dos recicladores tomen la misma).
    override suspend fun acceptRequest(id: String, recyclerId: String) {
        requests.update { list ->
            list.map {
                if (it.id == id && it.status == RequestStatus.AVAILABLE) {
                    it.copy(status = RequestStatus.ACCEPTED, recyclerId = recyclerId)
                } else it
            }
        }
    }

    override suspend fun completeRequest(id: String) {
        requests.update { list ->
            list.map {
                if (it.id == id && it.status == RequestStatus.ACCEPTED) {
                    it.copy(status = RequestStatus.COMPLETED)
                } else it
            }
        }
    }

    override suspend fun cancelRequest(id: String) {
        requests.update { list ->
            list.map {
                if (it.id == id &&
                    (it.status == RequestStatus.AVAILABLE || it.status == RequestStatus.ACCEPTED)
                ) {
                    it.copy(status = RequestStatus.CANCELLED)
                } else it
            }
        }
    }
}