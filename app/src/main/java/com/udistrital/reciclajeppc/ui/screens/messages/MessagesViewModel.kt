package com.udistrital.reciclajeppc.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MessagesUiState(
    val isLoading: Boolean = true,
    val conversations: List<Conversation> = emptyList()
)

class MessagesViewModel(
    messageRepository: MessageRepository,
    sessionManager: SessionManager
) : ViewModel() {

    val uiState: StateFlow<MessagesUiState> = combine(
        messageRepository.observeConversations(),
        sessionManager.activeRole
    ) { conversations, role ->
        MessagesUiState(
            isLoading = false,
            conversations = conversations.filter { it.myRole == role }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MessagesUiState()
    )

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MessagesViewModel(container.messageRepository, container.sessionManager)
            }
        }
    }
}