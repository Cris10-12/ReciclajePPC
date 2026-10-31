package com.udistrital.reciclajeppc.ui.screens.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MyRequestsUiState(
    val isLoading: Boolean = true,
    /** null = todas las activas */
    val filter: RequestStatus? = null,
    val requests: List<RecycleRequest> = emptyList()
)

class MyRequestsViewModel(
    requestRepository: RequestRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val filter = MutableStateFlow<RequestStatus?>(null)

    val uiState: StateFlow<MyRequestsUiState> = combine(
        requestRepository.observeRequests(),
        sessionManager.currentUser,
        filter
    ) { requests, user, selected ->
        MyRequestsUiState(
            isLoading = false,
            filter = selected,
            requests = requests.filter {
                user != null &&
                        it.generatorId == user.id &&
                        (it.status == RequestStatus.AVAILABLE || it.status == RequestStatus.ACCEPTED) &&
                        (selected == null || it.status == selected)
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MyRequestsUiState()
    )

    fun onFilterChange(status: RequestStatus?) {
        filter.value = status
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MyRequestsViewModel(container.requestRepository, container.sessionManager)
            }
        }
    }
}