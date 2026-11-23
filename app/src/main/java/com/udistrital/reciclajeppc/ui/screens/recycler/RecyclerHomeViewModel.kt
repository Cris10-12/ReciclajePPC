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

data class RecyclerHomeUiState(
    val userName: String = "",
    val availableRequests: List<RecycleRequest> = emptyList(),
    val acceptedRequests: List<RecycleRequest> = emptyList(),
    val completedCount: Int = 0,
    val collectedKg: Double = 0.0,
    val users: Map<String, User> = emptyMap()
)

class RecyclerHomeViewModel(
    requestRepository: RequestRepository,
    sessionManager: SessionManager,
    userRepository: UserRepository
) : ViewModel() {

    val uiState: StateFlow<RecyclerHomeUiState> = combine(
        requestRepository.observeRequests(),
        sessionManager.currentUser,
        userRepository.observeUsers()
    ) { requests, user, users ->
        val mine = requests.filter { user != null && it.recyclerId == user.id }
        val completed = mine.filter { it.status == RequestStatus.COMPLETED }
        RecyclerHomeUiState(
            userName = user?.name?.substringBefore(" ").orEmpty(),
            availableRequests = requests.filter {
                it.status == RequestStatus.AVAILABLE && it.generatorId != user?.id
            },
            acceptedRequests = mine.filter { it.status == RequestStatus.ACCEPTED },
            completedCount = completed.size,
            collectedKg = completed.sumOf { it.weightKg },
            users = users.associateBy { it.id }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecyclerHomeUiState()
    )

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RecyclerHomeViewModel(
                    container.requestRepository,
                    container.sessionManager,
                    container.userRepository
                )
            }
        }
    }
}