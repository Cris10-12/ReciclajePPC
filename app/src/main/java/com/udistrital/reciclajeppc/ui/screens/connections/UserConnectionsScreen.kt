package com.udistrital.reciclajeppc.ui.screens.connections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
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
fun UserConnectionsScreen(
    state: UserConnectionsUiState,
    onUserClick: (String) -> Unit,
    onBack: () -> Unit,
    onCloseTrail: (() -> Unit)? = null
) {
    val title = if (state.ownerName.isBlank()) {
        AppTexts.TAB_CONNECTED
    } else {
        AppTexts.USER_CONNECTIONS_TITLE.format(state.ownerName.substringBefore(" "))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = title,
            onBack = onBack,
            actions = {
                if (onCloseTrail != null) {
                    IconButton(onClick = onCloseTrail) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = AppTexts.CLOSE_TRAIL
                        )
                    }
                }
            }
        )

        if (!state.isLoading && state.users.isEmpty()) {
            EmptyState(
                message = AppTexts.EMPTY_USER_CONNECTIONS,
                hint = AppTexts.EMPTY_USER_CONNECTIONS_HINT,
                icon = Icons.Filled.Group
            )
        } else {
            Text(
                text = pluralize(state.users.size, "conexión", "conexiones"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = Dimens.spaceMd,
                    vertical = Dimens.spaceSm
                )
            )
            LazyColumn(modifier = Modifier.weight(1f).screenPadding()) {
                items(state.users, key = { it.id }) { user ->
                    UserRow(
                        user = user,
                        subtitle = describeUser(user),
                        onClick = { onUserClick(user.id) }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}