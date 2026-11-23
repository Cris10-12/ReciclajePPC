package com.udistrital.reciclajeppc.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.Favorite
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.SocialRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavoriteItem(val user: User, val createdAt: Long)

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val items: List<FavoriteItem> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModel(
    private val sessionManager: SessionManager,
    private val socialRepository: SocialRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val myFavorites: Flow<List<Favorite>> =
        sessionManager.currentUser.flatMapLatest { me ->
            if (me == null) flowOf(emptyList()) else socialRepository.observeFavorites(me.id)
        }

    val uiState: StateFlow<FavoritesUiState> = combine(
        myFavorites,
        userRepository.observeUsers()
    ) { favorites, users ->
        FavoritesUiState(
            isLoading = false,
            items = favorites
                .sortedByDescending { it.createdAt }
                .mapNotNull { favorite ->
                    users.firstOrNull { it.id == favorite.favoriteUserId }
                        ?.let { FavoriteItem(it, favorite.createdAt) }
                }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FavoritesUiState()
    )

    fun removeFavorite(userId: String) {
        val me = sessionManager.currentUser.value ?: return
        viewModelScope.launch { socialRepository.toggleFavorite(me.id, userId) }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FavoritesViewModel(
                    container.sessionManager,
                    container.socialRepository,
                    container.userRepository
                )
            }
        }
    }
}