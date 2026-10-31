package com.udistrital.reciclajeppc.ui.screens.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.MaterialType
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CreateRequestUiState(
    val materials: Set<MaterialType> = emptySet(),
    val weight: String = "",
    val address: String = "",
    val neighborhood: String = "",
    val availability: String = "",
    val notes: String = "",
    val materialError: String? = null,
    val weightError: String? = null,
    val addressError: String? = null,
    val neighborhoodError: String? = null,
    val availabilityError: String? = null,
    val isSubmitting: Boolean = false,
    val isPublished: Boolean = false
)

class CreateRequestViewModel(
    private val requestRepository: RequestRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRequestUiState())
    val uiState: StateFlow<CreateRequestUiState> = _uiState.asStateFlow()

    fun onMaterialToggle(value: MaterialType) = _uiState.update {
        val updated = if (value in it.materials) it.materials - value else it.materials + value
        it.copy(materials = updated, materialError = null)
    }
    fun onWeightChange(value: String) =
        _uiState.update { it.copy(weight = value, weightError = null) }

    fun onAddressChange(value: String) =
        _uiState.update { it.copy(address = value, addressError = null) }

    fun onNeighborhoodChange(value: String) =
        _uiState.update { it.copy(neighborhood = value, neighborhoodError = null) }

    fun onAvailabilityChange(value: String) =
        _uiState.update { it.copy(availability = value, availabilityError = null) }

    fun onNotesChange(value: String) =
        _uiState.update { it.copy(notes = value) }

    fun submit() {
        val s = _uiState.value
        if (s.isSubmitting || s.isPublished) return

        val weightKg = s.weight.trim().replace(',', '.').toDoubleOrNull()
        val weightError = when {
            s.weight.isBlank() -> AppTexts.ERR_WEIGHT_EMPTY
            weightKg == null || weightKg < 0.1 || weightKg > 1000.0 -> AppTexts.ERR_WEIGHT_INVALID
            else -> null
        }
        val materialError = if (s.materials.isEmpty()) AppTexts.ERR_MATERIAL else null
        val addressError = if (s.address.isBlank()) AppTexts.ERR_ADDRESS else null
        val neighborhoodError = if (s.neighborhood.isBlank()) AppTexts.ERR_NEIGHBORHOOD else null
        val availabilityError = if (s.availability.isBlank()) AppTexts.ERR_AVAILABILITY else null

        val hasErrors = listOf(
            materialError, weightError, addressError, neighborhoodError, availabilityError
        ).any { it != null }

        if (hasErrors) {
            _uiState.update {
                it.copy(
                    materialError = materialError,
                    weightError = weightError,
                    addressError = addressError,
                    neighborhoodError = neighborhoodError,
                    availabilityError = availabilityError
                )
            }
            return
        }

        val user = sessionManager.currentUser.value ?: return
// Conserva el orden del enum para que el material principal sea predecible.
        val selected = MaterialType.entries.filter { it in s.materials }
        val material = selected.firstOrNull() ?: return
        val weight = weightKg ?: return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            requestRepository.createRequest(
                RecycleRequest(
                    id = "r-" + UUID.randomUUID().toString().take(8),
                    generatorId = user.id,
                    generatorName = user.name,
                    material = material,
                    weightKg = weight,
                    address = s.address.trim(),
                    neighborhood = s.neighborhood.trim(),
                    availability = s.availability.trim(),
                    notes = s.notes.trim(),
                    status = RequestStatus.AVAILABLE
                )
            )
            _uiState.update { it.copy(isSubmitting = false, isPublished = true) }
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CreateRequestViewModel(container.requestRepository, container.sessionManager)
            }
        }
    }
}