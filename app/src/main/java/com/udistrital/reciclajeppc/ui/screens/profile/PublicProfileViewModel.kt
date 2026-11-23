package com.udistrital.reciclajeppc.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.core.util.chatErrorText
import com.udistrital.reciclajeppc.domain.model.Connection
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.model.Favorite
import com.udistrital.reciclajeppc.domain.model.Photo
import com.udistrital.reciclajeppc.domain.model.Recommendation
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import com.udistrital.reciclajeppc.domain.repository.PhotoRepository
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

enum class ConnectionRelation { NONE, REQUEST_SENT, REQUEST_RECEIVED, CONNECTED }

data class RecommendationItem(
    val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUri: String?,
    val comment: String,
    val createdAt: Long,
    val isMine: Boolean
)

data class PublicProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val connectionCount: Int = 0,
    val recommendations: List<RecommendationItem> = emptyList(),
    val photoCount: Int = 0,
    val previewPhotos: List<Photo> = emptyList(),
    val relation: ConnectionRelation = ConnectionRelation.NONE,
    val connectionId: String? = null,
    val isFavorite: Boolean = false,
    val hasRecommendedByMe: Boolean = false,
    val canMessage: Boolean = false,
    val existingConversationId: String? = null,
    val chatRequestId: String? = null,
    val chatError: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class PublicProfileViewModel(
    private val userId: String,
    private val sessionManager: SessionManager,
    private val socialRepository: SocialRepository,
    private val messageRepository: MessageRepository,
    requestRepository: RequestRepository,
    userRepository: UserRepository,
    photoRepository: PhotoRepository
) : ViewModel() {

    private data class ProfileData(
        val user: User?,
        val users: List<User>,
        val connections: List<Connection>,
        val recommendations: List<Recommendation>,
        val photos: List<Photo>
    )

    private data class ChatContext(
        val requests: List<RecycleRequest>,
        val conversations: List<Conversation>
    )

    private val chatError = MutableStateFlow<String?>(null)
    private var openingChat = false

    private val profileData: Flow<ProfileData> = combine(
        userRepository.observeUser(userId),
        userRepository.observeUsers(),
        socialRepository.observeConnections(userId),
        socialRepository.observeRecommendationsReceived(userId),
        photoRepository.observePhotos(userId)
    ) { user, users, connections, recommendations, photos ->
        ProfileData(user, users, connections, recommendations, photos)
    }

    private val myFavorites: Flow<List<Favorite>> =
        sessionManager.currentUser.flatMapLatest { me ->
            if (me == null) flowOf(emptyList()) else socialRepository.observeFavorites(me.id)
        }

    private val chatContext: Flow<ChatContext> =
        sessionManager.currentUser.flatMapLatest { me ->
            if (me == null) {
                flowOf(ChatContext(emptyList(), emptyList()))
            } else {
                combine(
                    requestRepository.observeRequests(),
                    messageRepository.observeConversations(me.id)
                ) { requests, conversations -> ChatContext(requests, conversations) }
            }
        }

    val uiState: StateFlow<PublicProfileUiState> = combine(
        profileData,
        sessionManager.currentUser,
        myFavorites,
        chatContext,
        chatError
    ) { data, me, favorites, chat, chatErr ->
        val myId = me?.id
        val link = data.connections.firstOrNull { it.otherUserId(userId) == myId }
        val relation = when {
            link == null -> ConnectionRelation.NONE
            link.status == ConnectionStatus.ACCEPTED -> ConnectionRelation.CONNECTED
            link.status == ConnectionStatus.PENDING && link.requesterId == myId ->
                ConnectionRelation.REQUEST_SENT
            link.status == ConnectionStatus.PENDING -> ConnectionRelation.REQUEST_RECEIVED
            else -> ConnectionRelation.NONE // rechazada: se puede volver a enviar
        }

        // ---- Chat: una conversación existente con esta persona, o una solicitud compartida ----
        val existingConversationId = if (myId == null) null else {
            chat.conversations
                .filter { it.otherUserId(myId) == userId }
                .maxByOrNull { it.lastMessage?.sentAt ?: 0L }
                ?.id
        }
        val sharedRequests = if (myId == null) emptyList() else {
            chat.requests.filter {
                (it.generatorId == myId && it.recyclerId == userId) ||
                        (it.generatorId == userId && it.recyclerId == myId)
            }
        }
        val chatRequestId =
            sharedRequests.firstOrNull { it.status == RequestStatus.ACCEPTED }?.id
                ?: sharedRequests.firstOrNull { it.status == RequestStatus.COMPLETED }?.id

        PublicProfileUiState(
            isLoading = false,
            user = data.user,
            connectionCount = data.connections.count { it.status == ConnectionStatus.ACCEPTED },
            recommendations = data.recommendations.map { r ->
                val author = data.users.firstOrNull { it.id == r.authorId }
                RecommendationItem(
                    id = r.id,
                    authorId = r.authorId,
                    authorName = author?.name ?: AppTexts.UNKNOWN_USER,
                    authorAvatarUri = author?.avatarUri,
                    comment = r.comment,
                    createdAt = r.createdAt,
                    isMine = r.authorId == myId
                )
            },
            photoCount = data.photos.size,
            previewPhotos = data.photos.take(PREVIEW_PHOTOS),
            relation = relation,
            connectionId = link?.id,
            isFavorite = favorites.any { it.favoriteUserId == userId },
            hasRecommendedByMe = data.recommendations.any { it.authorId == myId },
            canMessage = existingConversationId != null || chatRequestId != null,
            existingConversationId = existingConversationId,
            chatRequestId = chatRequestId,
            chatError = chatErr
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PublicProfileUiState()
    )

    fun toggleFavorite() {
        val me = sessionManager.currentUser.value ?: return
        viewModelScope.launch { socialRepository.toggleFavorite(me.id, userId) }
    }

    fun sendRequest() {
        val me = sessionManager.currentUser.value ?: return
        viewModelScope.launch { socialRepository.sendRequest(me.id, userId) }
    }

    fun acceptRequest() = respond(accept = true)

    fun rejectRequest() = respond(accept = false)

    private fun respond(accept: Boolean) {
        val id = uiState.value.connectionId ?: return
        viewModelScope.launch { socialRepository.respond(id, accept) }
    }

    /** Cancela la solicitud enviada o elimina la conexión aceptada. */
    fun removeConnection() {
        val id = uiState.value.connectionId ?: return
        viewModelScope.launch { socialRepository.removeConnection(id) }
    }

    /**
     * Abre el chat con esta persona: usa la conversación existente y, si no hay,
     * crea la de la solicitud compartida. Avisa con el id de la conversación.
     */
    fun openChat(onOpen: (String) -> Unit) {
        val me = sessionManager.currentUser.value ?: return
        if (openingChat) return
        chatError.value = null

        val state = uiState.value
        val existing = state.existingConversationId
        if (existing != null) {
            onOpen(existing)
            return
        }
        val requestId = state.chatRequestId ?: return

        openingChat = true
        viewModelScope.launch {
            try {
                messageRepository.getOrCreateConversation(requestId, me.id)
                    .onSuccess { onOpen(it) }
                    .onFailure { chatError.value = it.chatErrorText() }
            } finally {
                openingChat = false
            }
        }
    }

    companion object {
        const val PREVIEW_PHOTOS = 6

        fun factory(container: AppContainer, userId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PublicProfileViewModel(
                        userId,
                        container.sessionManager,
                        container.socialRepository,
                        container.messageRepository,
                        container.requestRepository,
                        container.userRepository,
                        container.photoRepository
                    )
                }
            }
    }
}