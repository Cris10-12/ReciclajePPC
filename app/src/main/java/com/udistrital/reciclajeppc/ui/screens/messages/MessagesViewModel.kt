package com.udistrital.reciclajeppc.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.core.util.forRole
import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class ConversationItem(
    val id: String,
    val contact: User?,
    val request: RecycleRequest?,
    val lastMessage: String,
    val lastMessageAt: Long?,
    val unread: Int
)

data class MessagesUiState(
    val isLoading: Boolean = true,
    val conversations: List<ConversationItem> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesViewModel(
    messageRepository: MessageRepository,
    requestRepository: RequestRepository,
    userRepository: UserRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val myConversations: Flow<List<Conversation>> =
        sessionManager.currentUser.flatMapLatest { me ->
            if (me == null) flowOf(emptyList()) else messageRepository.observeConversations(me.id)
        }

    val uiState: StateFlow<MessagesUiState> = combine(
        myConversations,
        requestRepository.observeRequests(),
        userRepository.observeUsers(),
        sessionManager.currentUser,
        sessionManager.activeRole
    ) { conversations, requests, users, me, role ->
        val myId = me?.id
        val items = if (myId == null) {
            emptyList()
        } else {
            conversations
                .forRole(requests, myId, role)
                .map { conversation ->
                    val last = conversation.lastMessage
                    ConversationItem(
                        id = conversation.id,
                        contact = conversation.otherUserId(myId)
                            ?.let { id -> users.firstOrNull { it.id == id } },
                        request = requests.firstOrNull { it.id == conversation.requestId },
                        lastMessage = last?.text.orEmpty(),
                        lastMessageAt = last?.sentAt,
                        unread = conversation.unreadFor(myId)
                    )
                }
                .sortedByDescending { it.lastMessageAt ?: 0L }
        }
        MessagesUiState(isLoading = false, conversations = items)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MessagesUiState()
    )

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MessagesViewModel(
                    container.messageRepository,
                    container.requestRepository,
                    container.userRepository,
                    container.sessionManager
                )
            }
        }
    }
}