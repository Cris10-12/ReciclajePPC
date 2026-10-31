package com.udistrital.reciclajeppc.ui.screens.recycler

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AgendaUiState(
    val isLoading: Boolean = true,
    val requests: List<RecycleRequest> = emptyList()
)

class AgendaViewModel(
    requestRepository: RequestRepository,
    sessionManager: SessionManager
) : ViewModel() {

    val uiState: StateFlow<AgendaUiState> = combine(
        requestRepository.observeRequests(),
        sessionManager.currentUser
    ) { requests, user ->
        AgendaUiState(
            isLoading = false,
            requests = requests.filter {
                user != null &&
                        it.recyclerName == user.name &&
                        it.status == RequestStatus.ACCEPTED
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AgendaUiState()
    )

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AgendaViewModel(container.requestRepository, container.sessionManager)
            }
        }
    }
}