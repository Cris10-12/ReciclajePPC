package com.udistrital.reciclajeppc.ui.screens.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.domain.model.Connection
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.repository.SocialRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ConnectionsTab { CONNECTED, RECEIVED, SENT }

data class ConnectionItem(
    val connectionId: String,
    val user: User,
    val timestamp: Long
)

data class ConnectionsUiState(
    val isLoading: Boolean = true,
    val tab: ConnectionsTab = ConnectionsTab.CONNECTED,
    val connected: List<ConnectionItem> = emptyList(),
    val received: List<ConnectionItem> = emptyList(),
    val sent: List<ConnectionItem> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectionsViewModel(
    sessionManager: SessionManager,
    private val socialRepository: SocialRepository,
    userRepository: UserRepository,
    initialTab: ConnectionsTab = ConnectionsTab.CONNECTED
) : ViewModel() {

    private val tab = MutableStateFlow(initialTab)

    private val myConnections = sessionManager.currentUser.flatMapLatest { me ->
        if (me == null) flowOf(emptyList<Connection>())
        else socialRepository.observeConnections(me.id)
    }

    val uiState: StateFlow<ConnectionsUiState> = combine(
        myConnections,
        userRepository.observeUsers(),
        sessionManager.currentUser,
        tab
    ) { connections, users, me, selected ->
        val myId = me?.id
        if (myId == null) {
            ConnectionsUiState(isLoading = false, tab = selected)
        } else {
            fun toItem(c: Connection, timestamp: Long): ConnectionItem? =
                users.firstOrNull { it.id == c.otherUserId(myId) }
                    ?.let { ConnectionItem(c.id, it, timestamp) }

            ConnectionsUiState(
                isLoading = false,
                tab = selected,
                connected = connections
                    .filter { it.status == ConnectionStatus.ACCEPTED }
                    .sortedByDescending { it.respondedAt ?: it.createdAt }
                    .mapNotNull { toItem(it, it.respondedAt ?: it.createdAt) },
                received = connections
                    .filter { it.status == ConnectionStatus.PENDING && it.receiverId == myId }
                    .sortedByDescending { it.createdAt }
                    .mapNotNull { toItem(it, it.createdAt) },
                sent = connections
                    .filter { it.status == ConnectionStatus.PENDING && it.requesterId == myId }
                    .sortedByDescending { it.createdAt }
                    .mapNotNull { toItem(it, it.createdAt) }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ConnectionsUiState()
    )

    fun onTabChange(value: ConnectionsTab) {
        tab.value = value
    }

    fun accept(connectionId: String) {
        viewModelScope.launch { socialRepository.respond(connectionId, true) }
    }

    fun reject(connectionId: String) {
        viewModelScope.launch { socialRepository.respond(connectionId, false) }
    }

    /** Cancela una solicitud enviada. */
    fun cancelSent(connectionId: String) {
        viewModelScope.launch { socialRepository.removeConnection(connectionId) }
    }

    /** Elimina una conexión aceptada. */
    fun remove(connectionId: String) {
        viewModelScope.launch { socialRepository.removeConnection(connectionId) }
    }

    companion object {
        fun factory(
            container: AppContainer,
            initialTab: ConnectionsTab = ConnectionsTab.CONNECTED
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ConnectionsViewModel(
                    container.sessionManager,
                    container.socialRepository,
                    container.userRepository,
                    initialTab
                )
            }
        }
    }
}