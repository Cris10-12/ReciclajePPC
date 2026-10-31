package com.udistrital.reciclajeppc.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatUiState(
    val isLoading: Boolean = true,
    val conversation: Conversation? = null,
    val draft: String = ""
) {
    val canSend: Boolean get() = draft.isNotBlank()
}

class ChatViewModel(
    private val conversationId: String,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val draft = MutableStateFlow("")

    init {
        // Al abrir el chat, los mensajes pasan a leídos.
        viewModelScope.launch { messageRepository.markAsRead(conversationId) }
    }

    val uiState: StateFlow<ChatUiState> = combine(
        messageRepository.observeConversation(conversationId),
        draft
    ) { conversation, text ->
        ChatUiState(isLoading = false, conversation = conversation, draft = text)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChatUiState()
    )

    fun onDraftChange(value: String) {
        draft.value = value
    }

    fun send() {
        val text = draft.value.trim()
        if (text.isEmpty()) return
        draft.value = ""
        viewModelScope.launch { messageRepository.sendMessage(conversationId, text) }
    }

    companion object {
        fun factory(container: AppContainer, conversationId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ChatViewModel(conversationId, container.messageRepository) }
            }
    }
}