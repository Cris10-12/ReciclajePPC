package com.udistrital.reciclajeppc.ui.screens.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.core.util.normalizeForSearch
import com.udistrital.reciclajeppc.domain.model.Connection
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.model.UserRole
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

enum class PeopleRoleFilter { ALL, GENERATOR, RECYCLER }

data class PersonItem(
    val user: User,
    val isFavorite: Boolean
)

data class PeopleUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val roleFilter: PeopleRoleFilter = PeopleRoleFilter.ALL,
    val people: List<PersonItem> = emptyList(),
    val connectionCount: Int = 0,
    val favoriteCount: Int = 0,
    val pendingCount: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class PeopleViewModel(
    private val sessionManager: SessionManager,
    private val socialRepository: SocialRepository,
    userRepository: UserRepository
) : ViewModel() {

    private data class Filters(val query: String, val roleFilter: PeopleRoleFilter)

    private data class SocialData(
        val favoriteIds: Set<String> = emptySet(),
        val connections: List<Connection> = emptyList()
    )

    private val query = MutableStateFlow("")
    private val roleFilter = MutableStateFlow(PeopleRoleFilter.ALL)

    private val filters: Flow<Filters> = combine(query, roleFilter) { text, role ->
        Filters(text, role)
    }

    private val socialData: Flow<SocialData> = sessionManager.currentUser.flatMapLatest { me ->
        if (me == null) {
            flowOf(SocialData())
        } else {
            combine(
                socialRepository.observeFavorites(me.id),
                socialRepository.observeConnections(me.id)
            ) { favorites, connections ->
                SocialData(
                    favoriteIds = favorites.map { it.favoriteUserId }.toSet(),
                    connections = connections
                )
            }
        }
    }

    val uiState: StateFlow<PeopleUiState> = combine(
        userRepository.observeUsers(),
        sessionManager.currentUser,
        filters,
        socialData
    ) { users, me, currentFilters, social ->
        val myId = me?.id
        val tokens = currentFilters.query
            .normalizeForSearch()
            .split(" ")
            .filter { it.isNotBlank() }

        val people = users
            .filter { it.id != myId }
            .filter { matchesRole(it, currentFilters.roleFilter) }
            .filter { user ->
                val haystack = "${user.name} ${user.neighborhood}".normalizeForSearch()
                tokens.all { haystack.contains(it) }
            }
            .sortedBy { it.name.normalizeForSearch() }
            .map { PersonItem(user = it, isFavorite = it.id in social.favoriteIds) }

        PeopleUiState(
            isLoading = false,
            query = currentFilters.query,
            roleFilter = currentFilters.roleFilter,
            people = people,
            connectionCount = social.connections.count { it.status == ConnectionStatus.ACCEPTED },
            favoriteCount = social.favoriteIds.size,
            pendingCount = social.connections.count {
                it.status == ConnectionStatus.PENDING && it.receiverId == myId
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PeopleUiState()
    )

    fun onQueryChange(value: String) {
        query.value = value.take(MAX_QUERY_LENGTH)
    }

    fun onRoleFilterChange(value: PeopleRoleFilter) {
        roleFilter.value = value
    }

    fun toggleFavorite(targetId: String) {
        val me = sessionManager.currentUser.value ?: return
        viewModelScope.launch { socialRepository.toggleFavorite(me.id, targetId) }
    }

    private fun matchesRole(user: User, filter: PeopleRoleFilter): Boolean = when (filter) {
        PeopleRoleFilter.ALL -> true
        PeopleRoleFilter.GENERATOR -> UserRole.GENERATOR in user.roles
        PeopleRoleFilter.RECYCLER -> UserRole.RECYCLER in user.roles
    }

    companion object {
        const val MAX_QUERY_LENGTH = 60

        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PeopleViewModel(
                    container.sessionManager,
                    container.socialRepository,
                    container.userRepository
                )
            }
        }
    }
}