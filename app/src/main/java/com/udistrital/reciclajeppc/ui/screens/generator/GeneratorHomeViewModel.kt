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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class GeneratorHomeUiState(
    val userName: String = "",
    val activeRequests: List<RecycleRequest> = emptyList(),
    val completedCount: Int = 0,
    val recycledKg: Double = 0.0
)

class GeneratorHomeViewModel(
    requestRepository: RequestRepository,
    sessionManager: SessionManager
) : ViewModel() {

    val uiState: StateFlow<GeneratorHomeUiState> = combine(
        requestRepository.observeRequests(),
        sessionManager.currentUser
    ) { requests, user ->
        val mine = requests.filter { it.generatorId == user?.id }
        val completed = mine.filter { it.status == RequestStatus.COMPLETED }
        GeneratorHomeUiState(
            userName = user?.name?.substringBefore(" ").orEmpty(),
            activeRequests = mine.filter {
                it.status == RequestStatus.AVAILABLE || it.status == RequestStatus.ACCEPTED
            },
            completedCount = completed.size,
            recycledKg = completed.sumOf { it.weightKg }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GeneratorHomeUiState()
    )

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                GeneratorHomeViewModel(container.requestRepository, container.sessionManager)
            }
        }
    }
}