package com.udistrital.reciclajeppc.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.model.UserRole
import com.udistrital.reciclajeppc.domain.repository.PhotoRepository
import com.udistrital.reciclajeppc.domain.repository.SocialRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
    val user: User? = null,
    val role: UserRole = UserRole.GENERATOR,
    val connectionCount: Int = 0,
    val favoriteCount: Int = 0,
    val recommendationCount: Int = 0,
    val photoCount: Int = 0,
    val pendingRequestCount: Int = 0,
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    sessionManager: SessionManager,
    socialRepository: SocialRepository,
    photoRepository: PhotoRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        sessionManager.currentUser,
        sessionManager.activeRole
    ) { user, role -> user to role }
        .flatMapLatest { (user, role) ->
            if (user == null) {
                flowOf(ProfileUiState(role = role))
            } else {
                combine(
                    socialRepository.observeConnections(user.id),
                    socialRepository.observeFavorites(user.id),
                    socialRepository.observeRecommendationsReceived(user.id),
                    photoRepository.observePhotos(user.id)
                ) { connections, favorites, recommendations, photos ->
                    ProfileUiState(
                        user = user,
                        role = role,
                        connectionCount = connections.count {
                            it.status == ConnectionStatus.ACCEPTED
                        },
                        pendingRequestCount = connections.count {
                            it.status == ConnectionStatus.PENDING && it.receiverId == user.id
                        },
                        favoriteCount = favorites.size,
                        recommendationCount = recommendations.size,
                        photoCount = photos.size
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState()
        )

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ProfileViewModel(
                    container.sessionManager,
                    container.socialRepository,
                    container.photoRepository
                )
            }
        }
    }
}