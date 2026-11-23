package com.udistrital.reciclajeppc.ui.screens.people

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.describeUser
import com.udistrital.reciclajeppc.core.util.pluralize
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTextField
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.MaterialChip
import com.udistrital.reciclajeppc.ui.components.MessagesAction
import com.udistrital.reciclajeppc.ui.components.StatCard
import com.udistrital.reciclajeppc.ui.components.UserRow

@Composable
fun PeopleScreen(
    state: PeopleUiState,
    unreadMessages: Int,
    onMessages: () -> Unit,
    onQueryChange: (String) -> Unit,
    onRoleFilterChange: (PeopleRoleFilter) -> Unit,
    onUserClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onConnections: () -> Unit,
    onFavorites: () -> Unit,
    onRequests: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = AppTexts.PEOPLE_TITLE,
            actions = {
                MessagesAction(unreadCount = unreadMessages, onClick = onMessages)
            }
        )

        Column(
            modifier = Modifier.screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            AppTextField(
                value = state.query,
                onValueChange = onQueryChange,
                label = AppTexts.PEOPLE_SEARCH_HINT,
                leadingIcon = Icons.Filled.Search
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                listOf(
                    PeopleRoleFilter.ALL to AppTexts.PEOPLE_FILTER_ALL,
                    PeopleRoleFilter.GENERATOR to AppTexts.PEOPLE_FILTER_GENERATORS,
                    PeopleRoleFilter.RECYCLER to AppTexts.PEOPLE_FILTER_RECYCLERS
                ).forEach { (value, label) ->
                    MaterialChip(
                        label = label,
                        selected = state.roleFilter == value,
                        onClick = { onRoleFilterChange(value) }
                    )
                }
            }
            if (state.people.isNotEmpty()) {
                Text(
                    text = pluralize(state.people.size, "persona", "personas"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!state.isLoading && state.people.isEmpty()) {
            Column(modifier = Modifier.screenPadding()) {
                PeopleShortcuts(
                    state = state,
                    onConnections = onConnections,
                    onFavorites = onFavorites,
                    onRequests = onRequests
                )
            }
            EmptyState(
                message = AppTexts.PEOPLE_EMPTY,
                hint = AppTexts.PEOPLE_EMPTY_HINT,
                icon = Icons.Filled.Search
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f).screenPadding()) {
                item {
                    PeopleShortcuts(
                        state = state,
                        onConnections = onConnections,
                        onFavorites = onFavorites,
                        onRequests = onRequests
                    )
                }
                items(state.people, key = { it.user.id }) { item ->
                    Column {
                        UserRow(
                            user = item.user,
                            subtitle = describeUser(item.user),
                            onClick = { onUserClick(item.user.id) },
                            trailing = {
                                IconButton(onClick = { onToggleFavorite(item.user.id) }) {
                                    Icon(
                                        imageVector = if (item.isFavorite) Icons.Filled.Favorite
                                        else Icons.Filled.FavoriteBorder,
                                        contentDescription = if (item.isFavorite)
                                            AppTexts.REMOVE_FAVORITE else AppTexts.ADD_FAVORITE,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

/** Atajos con contadores: conexiones, favoritos y solicitudes recibidas. */
@Composable
private fun PeopleShortcuts(
    state: PeopleUiState,
    onConnections: () -> Unit,
    onFavorites: () -> Unit,
    onRequests: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.spaceSm),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
    ) {
        StatCard(
            value = state.connectionCount.toString(),
            label = AppTexts.STAT_CONNECTIONS,
            icon = Icons.Filled.Group,
            modifier = Modifier.weight(1f),
            onClick = onConnections
        )
        StatCard(
            value = state.favoriteCount.toString(),
            label = AppTexts.STAT_FAVORITES,
            icon = Icons.Filled.Favorite,
            modifier = Modifier.weight(1f),
            onClick = onFavorites
        )
        StatCard(
            value = state.pendingCount.toString(),
            label = AppTexts.PEOPLE_SHORTCUT_REQUESTS,
            icon = Icons.Filled.PersonAdd,
            modifier = Modifier.weight(1f),
            onClick = onRequests
        )
    }
}