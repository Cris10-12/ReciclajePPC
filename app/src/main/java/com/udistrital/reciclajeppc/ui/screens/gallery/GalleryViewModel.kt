package com.udistrital.reciclajeppc.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.Photo
import com.udistrital.reciclajeppc.domain.repository.PhotoRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GalleryUiState(
    val isLoading: Boolean = true,
    val ownerName: String = "",
    val photos: List<Photo> = emptyList(),
    val isOwner: Boolean = false,
    val limitReached: Boolean = false
)

class GalleryViewModel(
    private val ownerId: String,
    private val photoRepository: PhotoRepository,
    private val sessionManager: SessionManager,
    userRepository: UserRepository
) : ViewModel() {

    private val limitReached = MutableStateFlow(false)

    val uiState: StateFlow<GalleryUiState> = combine(
        photoRepository.observePhotos(ownerId),
        sessionManager.currentUser,
        userRepository.observeUser(ownerId),
        limitReached
    ) { photos, me, owner, limit ->
        GalleryUiState(
            isLoading = false,
            ownerName = owner?.name.orEmpty(),
            photos = photos,
            isOwner = me?.id == ownerId,
            limitReached = limit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GalleryUiState()
    )

    private fun isOwner(): Boolean = sessionManager.currentUser.value?.id == ownerId

    fun addPhotos(uris: List<String>) {
        if (!isOwner() || uris.isEmpty()) return
        val room = (MAX_PHOTOS - uiState.value.photos.size).coerceAtLeast(0)
        limitReached.value = uris.size > room
        val toAdd = uris.take(room)
        if (toAdd.isEmpty()) return
        viewModelScope.launch {
            toAdd.forEach { photoRepository.addPhoto(ownerId, it, "") }
        }
    }

    fun deletePhoto(photoId: String) {
        if (!isOwner()) return
        limitReached.value = false
        viewModelScope.launch { photoRepository.deletePhoto(photoId) }
    }

    companion object {
        const val MAX_PHOTOS = 30
        const val MAX_PER_PICK = 10

        fun factory(container: AppContainer, ownerId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    GalleryViewModel(
                        ownerId,
                        container.photoRepository,
                        container.sessionManager,
                        container.userRepository
                    )
                }
            }
    }
}