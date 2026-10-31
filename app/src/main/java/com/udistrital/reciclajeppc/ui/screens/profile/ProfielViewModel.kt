package com.udistrital.reciclajeppc.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.model.UserRole
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
    val user: User? = null,
    val role: UserRole = UserRole.GENERATOR
)

class ProfileViewModel(sessionManager: SessionManager) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        sessionManager.currentUser,
        sessionManager.activeRole
    ) { user, role ->
        ProfileUiState(user = user, role = role)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { ProfileViewModel(container.sessionManager) }
        }
    }
}