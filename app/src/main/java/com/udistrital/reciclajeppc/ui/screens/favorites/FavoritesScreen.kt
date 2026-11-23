package com.udistrital.reciclajeppc.ui.screens.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.UserRow

@Composable
fun FavoritesScreen(
    state: FavoritesUiState,
    onUserClick: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = AppTexts.FAVORITES_TITLE, onBack = onBack)

        if (!state.isLoading && state.items.isEmpty()) {
            EmptyState(
                message = AppTexts.EMPTY_FAVORITES,
                hint = AppTexts.EMPTY_FAVORITES_HINT,
                icon = Icons.Filled.FavoriteBorder
            )
        } else {
            Text(
                text = pluralize(state.items.size, "favorito", "favoritos"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = Dimens.spaceMd,
                    vertical = Dimens.spaceSm
                )
            )
            LazyColumn(modifier = Modifier.weight(1f).screenPadding()) {
                items(state.items, key = { it.user.id }) { item ->
                    UserRow(
                        user = item.user,
                        subtitle = describeUser(item.user),
                        onClick = { onUserClick(item.user.id) },
                        trailing = {
                            IconButton(onClick = { onRemoveFavorite(item.user.id) }) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = AppTexts.REMOVE_FAVORITE,
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