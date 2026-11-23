package com.udistrital.reciclajeppc.ui.screens.recommendations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.core.util.toSocialMessage
import com.udistrital.reciclajeppc.core.util.validateRecommendation
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.model.Recommendation
import com.udistrital.reciclajeppc.domain.model.User
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecommendUiState(
    val isLoading: Boolean = true,
    val recipient: User? = null,
    /** Mi recomendación hacia esta persona, si ya la hice. */
    val existing: Recommendation? = null,
    val canRecommend: Boolean = false,
    val isSelf: Boolean = false,
    val comment: String = "",
    val commentError: String? = null,
    val submitError: String? = null,
    val isSaving: Boolean = false,
    /** true cuando la operación terminó bien; la pantalla vuelve atrás. */
    val done: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendViewModel(
    private val userId: String,
    private val sessionManager: SessionManager,
    private val socialRepository: SocialRepository,
    userRepository: UserRepository
) : ViewModel() {

    private data class Access(val connected: Boolean = false, val isSelf: Boolean = false)

    private data class SaveState(
        val isSaving: Boolean = false,
        val showError: Boolean = false,
        val submitError: String? = null,
        val done: Boolean = false
    )

    /** Texto escrito por la persona; null = aún no ha tocado el campo. */
    private val draft = MutableStateFlow<String?>(null)
    private val saveState = MutableStateFlow(SaveState())

    private val recipientFlow = userRepository.observeUser(userId)

    private val accessFlow: Flow<Access> = sessionManager.currentUser.flatMapLatest { me ->
        if (me == null) {
            flowOf(Access())
        } else {
            socialRepository.observeConnections(me.id).map { list ->
                Access(
                    connected = list.any {
                        it.status == ConnectionStatus.ACCEPTED && it.involves(userId)
                    },
                    isSelf = me.id == userId
                )
            }
        }
    }

    private val existingFlow: Flow<Recommendation?> =
        sessionManager.currentUser.flatMapLatest { me ->
            if (me == null) {
                flowOf(null)
            } else {
                socialRepository.observeRecommendationsReceived(userId).map { list ->
                    list.firstOrNull { it.authorId == me.id }
                }
            }
        }

    val uiState: StateFlow<RecommendUiState> = combine(
        recipientFlow,
        accessFlow,
        existingFlow,
        draft,
        saveState
    ) { recipient, access, existing, draftText, save ->
        // Si no ha escrito nada, se muestra la recomendación actual (modo edición).
        val comment = draftText ?: existing?.comment.orEmpty()
        RecommendUiState(
            isLoading = false,
            recipient = recipient,
            existing = existing,
            canRecommend = access.connected && !access.isSelf,
            isSelf = access.isSelf,
            comment = comment,
            commentError = if (save.showError) validateRecommendation(comment) else null,
            submitError = save.submitError,
            isSaving = save.isSaving,
            done = save.done
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RecommendUiState()
    )

    fun onCommentChange(value: String) {
        draft.value = value.take(Recommendation.MAX_LENGTH)
        saveState.update { it.copy(submitError = null) }
    }

    /** Publica una recomendación nueva o guarda la edición de la existente. */
    fun submit() {
        val state = uiState.value
        val me = sessionManager.currentUser.value ?: return
        if (state.isSaving) return

        val text = state.comment.trim()
        if (validateRecommendation(text) != null) {
            saveState.update { it.copy(showError = true) }
            return
        }

        val existing = state.existing
        saveState.update { it.copy(isSaving = true, showError = false, submitError = null) }
        viewModelScope.launch {
            val result = if (existing != null) {
                socialRepository.updateRecommendation(existing.id, text)
            } else {
                socialRepository.recommend(me.id, userId, text)
            }
            finish(result)
        }
    }

    /** Retira mi recomendación (la pantalla ya pidió confirmación). */
    fun remove() {
        val state = uiState.value
        val existing = state.existing ?: return
        if (state.isSaving) return

        saveState.update { it.copy(isSaving = true, submitError = null) }
        viewModelScope.launch {
            finish(socialRepository.removeRecommendation(existing.id))
        }
    }

    private fun finish(result: Result<Unit>) {
        result
            .onSuccess {
                saveState.update { s -> s.copy(isSaving = false, done = true) }
            }
            .onFailure { error ->
                saveState.update { s ->
                    s.copy(isSaving = false, submitError = error.toSocialMessage())
                }
            }
    }

    companion object {
        fun factory(container: AppContainer, userId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    RecommendViewModel(
                        userId,
                        container.sessionManager,
                        container.socialRepository,
                        container.userRepository
                    )
                }
            }
    }
}