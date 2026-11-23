package com.udistrital.reciclajeppc.ui.screens.recycler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.MaterialType
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class ExploreUiState(
    val selectedMaterials: Set<MaterialType> = emptySet(),
    val requests: List<RecycleRequest> = emptyList(),
    val generators: Map<String, User> = emptyMap()
)

class ExploreViewModel(
    requestRepository: RequestRepository,
    sessionManager: SessionManager,
    userRepository: UserRepository
) : ViewModel() {

    private val selected = MutableStateFlow<Set<MaterialType>>(emptySet())

    val uiState: StateFlow<ExploreUiState> = combine(
        requestRepository.observeRequests(),
        sessionManager.currentUser,
        selected,
        userRepository.observeUsers()
    ) { requests, user, filter, users ->
        ExploreUiState(
            selectedMaterials = filter,
            requests = requests.filter { request ->
                request.status == RequestStatus.AVAILABLE &&
                        request.generatorId != user?.id &&
                        (filter.isEmpty() || request.materials.any { it in filter })
            },
            generators = users.associateBy { it.id }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExploreUiState()
    )

    fun onMaterialToggle(material: MaterialType) = selected.update {
        if (material in it) it - material else it + material
    }

    fun onClearFilter() {
        selected.value = emptySet()
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ExploreViewModel(
                    container.requestRepository,
                    container.sessionManager,
                    container.userRepository
                )
            }
        }
    }
}