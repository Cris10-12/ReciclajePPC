package com.udistrital.reciclajeppc.ui.screens.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.SocialRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class UserConnectionsUiState(
    val isLoading: Boolean = true,
    val ownerName: String = "",
    val users: List<User> = emptyList()
)

/** Conexiones aceptadas de cualquier persona, en solo lectura. */
class UserConnectionsViewModel(
    private val ownerId: String,
    socialRepository: SocialRepository,
    userRepository: UserRepository
) : ViewModel() {

    val uiState: StateFlow<UserConnectionsUiState> = combine(
        socialRepository.observeConnections(ownerId),
        userRepository.observeUsers()
    ) { connections, users ->
        val otherIds = connections
            .filter { it.status == ConnectionStatus.ACCEPTED }
            .map { it.otherUserId(ownerId) }
        UserConnectionsUiState(
            isLoading = false,
            ownerName = users.firstOrNull { it.id == ownerId }?.name.orEmpty(),
            users = otherIds
                .mapNotNull { id -> users.firstOrNull { it.id == id } }
                .sortedBy { it.name }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserConnectionsUiState()
    )

    companion object {
        fun factory(container: AppContainer, ownerId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    UserConnectionsViewModel(
                        ownerId,
                        container.socialRepository,
                        container.userRepository
                    )
                }
            }
    }
}