package com.udistrital.reciclajeppc.ui.screens.shared

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RequestDetailUiState(
    val isLoading: Boolean = true,
    val request: RecycleRequest? = null,
    val canAccept: Boolean = false,
    val canComplete: Boolean = false,
    val canCancel: Boolean = false,
    val isWorking: Boolean = false
)

class RequestDetailViewModel(
    private val requestId: String,
    private val requestRepository: RequestRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val working = MutableStateFlow(false)

    val uiState: StateFlow<RequestDetailUiState> = combine(
        requestRepository.observeRequest(requestId),
        sessionManager.currentUser,
        working
    ) { request, user, isWorking ->
        if (request == null) {
            RequestDetailUiState(isLoading = false, request = null)
        } else {
            val isOwner = request.generatorId == user?.id
            val isMine = request.recyclerName != null && request.recyclerName == user?.name
            RequestDetailUiState(
                isLoading = false,
                request = request,
                canAccept = !isOwner && request.status == RequestStatus.AVAILABLE,
                canComplete = !isOwner && isMine && request.status == RequestStatus.ACCEPTED,
                canCancel = isOwner &&
                        (request.status == RequestStatus.AVAILABLE ||
                                request.status == RequestStatus.ACCEPTED),
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
        runAction { requestRepository.acceptRequest(requestId, user.name) }
    }

    fun complete() = runAction { requestRepository.completeRequest(requestId) }

    fun cancel() = runAction { requestRepository.cancelRequest(requestId) }

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
                        container.sessionManager
                    )
                }
            }
    }
}