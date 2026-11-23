package com.udistrital.reciclajeppc.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val name: String = "",
    val phone: String = "",
    val neighborhood: String = "",
    val bio: String = "",
    val avatarUri: String? = null,
    val nameError: String? = null,
    val phoneError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val saveError: String? = null
)

class EditProfileViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private fun initialState(): EditProfileUiState {
        val user = sessionManager.currentUser.value
        return EditProfileUiState(
            name = user?.name.orEmpty(),
            phone = user?.phone.orEmpty(),
            neighborhood = user?.neighborhood.orEmpty(),
            bio = user?.bio.orEmpty(),
            avatarUri = user?.avatarUri
        )
    }

    fun onNameChange(value: String) =
        _uiState.update { it.copy(name = value.take(NAME_MAX), nameError = null) }

    fun onPhoneChange(value: String) = _uiState.update {
        it.copy(
            phone = value.filter { c -> c.isDigit() || c in " +-" }.take(PHONE_MAX),
            phoneError = null
        )
    }

    fun onNeighborhoodChange(value: String) =
        _uiState.update { it.copy(neighborhood = value.take(NEIGHBORHOOD_MAX)) }

    fun onBioChange(value: String) =
        _uiState.update { it.copy(bio = value.take(BIO_MAX)) }

    fun onAvatarChange(uri: String?) =
        _uiState.update { it.copy(avatarUri = uri) }

    fun save() {
        val s = _uiState.value
        if (s.isSaving || s.isSaved) return

        val nameError = if (s.name.isBlank()) AppTexts.ERR_NAME_EMPTY else null
        val phoneDigits = s.phone.count { it.isDigit() }
        val phoneError =
            if (s.phone.isNotBlank() && phoneDigits !in 7..15) AppTexts.ERR_PHONE_INVALID else null

        if (nameError != null || phoneError != null) {
            _uiState.update { it.copy(nameError = nameError, phoneError = phoneError) }
            return
        }

        _uiState.update { it.copy(isSaving = true, saveError = null) }
        viewModelScope.launch {
            sessionManager.updateProfile(
                name = s.name.trim(),
                phone = s.phone.trim(),
                neighborhood = s.neighborhood.trim(),
                bio = s.bio.trim(),
                avatarUri = s.avatarUri
            ).onSuccess {
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            }.onFailure {
                _uiState.update {
                    it.copy(isSaving = false, saveError = AppTexts.ERR_SAVE_PROFILE)
                }
            }
        }
    }

    companion object {
        const val NAME_MAX = 60
        const val PHONE_MAX = 20
        const val NEIGHBORHOOD_MAX = 40
        const val BIO_MAX = 160

        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { EditProfileViewModel(container.sessionManager) }
        }
    }
}