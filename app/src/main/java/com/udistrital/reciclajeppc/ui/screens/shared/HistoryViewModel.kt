package com.udistrital.reciclajeppc.ui.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.model.UserRole
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val isLoading: Boolean = true,
    val role: UserRole = UserRole.GENERATOR,
    /** null = completadas y canceladas */
    val filter: RequestStatus? = null,
    val requests: List<RecycleRequest> = emptyList(),
    val completedCount: Int = 0,
    val completedKg: Double = 0.0
)

class HistoryViewModel(
    requestRepository: RequestRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val filter = MutableStateFlow<RequestStatus?>(null)

    val uiState: StateFlow<HistoryUiState> = combine(
        requestRepository.observeRequests(),
        sessionManager.currentUser,
        sessionManager.activeRole,
        filter
    ) { requests, user, role, selected ->
        val closed = requests.filter { request ->
            val isClosed = request.status == RequestStatus.COMPLETED ||
                    request.status == RequestStatus.CANCELLED
            val isMine = when (role) {
                UserRole.GENERATOR -> user != null && request.generatorId == user.id
                UserRole.RECYCLER -> user != null && request.recyclerName == user.name
            }
            isClosed && isMine
        }
        val completed = closed.filter { it.status == RequestStatus.COMPLETED }
        HistoryUiState(
            isLoading = false,
            role = role,
            filter = selected,
            requests = closed.filter { selected == null || it.status == selected },
            completedCount = completed.size,
            completedKg = completed.sumOf { it.weightKg }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState()
    )

    fun onFilterChange(status: RequestStatus?) {
        filter.value = status
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HistoryViewModel(container.requestRepository, container.sessionManager)
            }
        }
    }
}