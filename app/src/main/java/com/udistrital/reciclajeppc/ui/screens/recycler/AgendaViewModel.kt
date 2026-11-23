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
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AgendaUiState(
    val isLoading: Boolean = true,
    val requests: List<RecycleRequest> = emptyList(),
    val users: Map<String, User> = emptyMap()
)

class AgendaViewModel(
    requestRepository: RequestRepository,
    sessionManager: SessionManager,
    userRepository: UserRepository
) : ViewModel() {

    val uiState: StateFlow<AgendaUiState> = combine(
        requestRepository.observeRequests(),
        sessionManager.currentUser,
        userRepository.observeUsers()
    ) { requests, user, users ->
        AgendaUiState(
            isLoading = false,
            requests = requests.filter {
                user != null &&
                        it.recyclerId == user.id &&
                        it.status == RequestStatus.ACCEPTED
            },
            users = users.associateBy { it.id }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AgendaUiState()
    )

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AgendaViewModel(
                    container.requestRepository,
                    container.sessionManager,
                    container.userRepository
                )
            }
        }
    }
}