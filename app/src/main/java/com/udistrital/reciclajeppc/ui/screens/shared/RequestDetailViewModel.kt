package com.udistrital.reciclajeppc.ui.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.core.util.chatErrorText
import com.udistrital.reciclajeppc.domain.model.Favorite
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import com.udistrital.reciclajeppc.domain.repository.SocialRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RequestDetailUiState(
    val isLoading: Boolean = true,
    val request: RecycleRequest? = null,
    val generator: User? = null,
    val recycler: User? = null,
    val currentUserId: String? = null,
    val favoriteIds: Set<String> = emptySet(),
    val canAccept: Boolean = false,
    val canComplete: Boolean = false,
    val canCancel: Boolean = false,
    val canChat: Boolean = false,
    val chatError: String? = null,
    val isWorking: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class RequestDetailViewModel(
    private val requestId: String,
    private val requestRepository: RequestRepository,
    private val sessionManager: SessionManager,
    private val socialRepository: SocialRepository,
    private val messageRepository: MessageRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val working = MutableStateFlow(false)
    private val chatError = MutableStateFlow<String?>(null)

    private val transient: Flow<Pair<Boolean, String?>> =
        combine(working, chatError) { isWorking, error -> isWorking to error }

    private val myFavorites: Flow<List<Favorite>> =
        sessionManager.currentUser.flatMapLatest { me ->
            if (me == null) flowOf(emptyList()) else socialRepository.observeFavorites(me.id)
        }

    val uiState: StateFlow<RequestDetailUiState> = combine(
        requestRepository.observeRequest(requestId),
        sessionManager.currentUser,
        transient,
        userRepository.observeUsers(),
        myFavorites
    ) { request, user, (isWorking, chatErr), users, favorites ->
        if (request == null) {
            RequestDetailUiState(isLoading = false, request = null)
        } else {
            val isOwner = request.generatorId == user?.id
            val isMine = user != null && request.recyclerId == user.id
            RequestDetailUiState(
                isLoading = false,
                request = request,
                generator = users.firstOrNull { it.id == request.generatorId },
                recycler = users.firstOrNull { it.id == request.recyclerId },
                currentUserId = user?.id,
                favoriteIds = favorites.map { it.favoriteUserId }.toSet(),
                canAccept = !isOwner && request.status == RequestStatus.AVAILABLE,
                canComplete = !isOwner && isMine && request.status == RequestStatus.ACCEPTED,
                canCancel = isOwner &&
                        (request.status == RequestStatus.AVAILABLE ||
                                request.status == RequestStatus.ACCEPTED),
                canChat = (isOwner || isMine) &&
                        request.recyclerId != null &&
                        (request.status == RequestStatus.ACCEPTED ||
                                request.status == RequestStatus.COMPLETED),
                chatError = chatErr,
                isWorking = isWorking
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RequestDetailUiState()
    )

    fun accept() {
        val user = sessionManager.currentUser.value ?: return
        runAction { requestRepository.acceptRequest(requestId, user.id) }
    }

    fun complete() = runAction { requestRepository.completeRequest(requestId) }

    fun cancel() = runAction { requestRepository.cancelRequest(requestId) }

    fun toggleFavorite(targetId: String) {
        val me = sessionManager.currentUser.value ?: return
        viewModelScope.launch { socialRepository.toggleFavorite(me.id, targetId) }
    }

    /** Abre (o crea) la conversación de esta solicitud y avisa con su id. */
    fun openChat(onOpen: (String) -> Unit) {
        val me = sessionManager.currentUser.value ?: return
        if (working.value) return
        chatError.value = null
        viewModelScope.launch {
            working.value = true
            try {
                messageRepository.getOrCreateConversation(requestId, me.id)
                    .onSuccess { onOpen(it) }
                    .onFailure { chatError.value = it.chatErrorText() }
            } finally {
                working.value = false
            }
        }
    }

    private fun runAction(block: suspend () -> Unit) {
        if (working.value) return
        viewModelScope.launch {
            working.value = true
            try {
                block()
            } finally {
                working.value = false
            }
        }
    }

    companion object {
        fun factory(container: AppContainer, requestId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    RequestDetailViewModel(
                        requestId,
                        container.requestRepository,
                        container.sessionManager,
                        container.socialRepository,
                        container.messageRepository,
                        container.userRepository
                    )
                }
            }
    }
}