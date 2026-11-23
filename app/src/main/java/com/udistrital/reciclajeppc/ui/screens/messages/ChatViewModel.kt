package com.udistrital.reciclajeppc.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatUiState(
    val isLoading: Boolean = true,
    val conversation: Conversation? = null,
    val request: RecycleRequest? = null,
    val contact: User? = null,
    val currentUserId: String? = null,
    val canWrite: Boolean = false,
    val draft: String = ""
) {
    val canSend: Boolean get() = canWrite && draft.isNotBlank()
}

class ChatViewModel(
    private val conversationId: String,
    private val messageRepository: MessageRepository,
    private val sessionManager: SessionManager,
    requestRepository: RequestRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val draft = MutableStateFlow("")

    init {
        val me = sessionManager.currentUser.value
        if (me != null) {
            viewModelScope.launch { messageRepository.markAsRead(conversationId, me.id) }
        }
    }

    val uiState: StateFlow<ChatUiState> = combine(
        messageRepository.observeConversation(conversationId),
        requestRepository.observeRequests(),
        userRepository.observeUsers(),
        sessionManager.currentUser,
        draft
    ) { conversation, requests, users, me, text ->
        val request = conversation?.let { c -> requests.firstOrNull { it.id == c.requestId } }
        val contact = if (conversation != null && me != null) {
            conversation.otherUserId(me.id)?.let { id -> users.firstOrNull { it.id == id } }
        } else null
        ChatUiState(
            isLoading = false,
            conversation = conversation,
            request = request,
            contact = contact,
            currentUserId = me?.id,
            canWrite = request != null && request.status != RequestStatus.CANCELLED,
            draft = text
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChatUiState()
    )

    fun onDraftChange(value: String) {
        draft.value = value
    }

    fun send() {
        val me = sessionManager.currentUser.value ?: return
        val text = draft.value.trim()
        if (text.isEmpty() || !uiState.value.canWrite) return
        viewModelScope.launch {
            messageRepository.sendMessage(conversationId, me.id, text)
                .onSuccess { draft.value = "" }
        }
    }

    companion object {
        fun factory(container: AppContainer, conversationId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    ChatViewModel(
                        conversationId,
                        container.messageRepository,
                        container.sessionManager,
                        container.requestRepository,
                        container.userRepository
                    )
                }
            }
    }
}