package com.udistrital.reciclajeppc.ui.screens.connections

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.describeUser
import com.udistrital.reciclajeppc.core.util.formatRelativeTime
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.MaterialChip
import com.udistrital.reciclajeppc.ui.components.UserRow

@Composable
fun ConnectionsScreen(
    state: ConnectionsUiState,
    onTabChange: (ConnectionsTab) -> Unit,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onCancelSent: (String) -> Unit,
    onRemove: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var connectionToRemove by rememberSaveable { mutableStateOf<String?>(null) }

    val items = when (state.tab) {
        ConnectionsTab.CONNECTED -> state.connected
        ConnectionsTab.RECEIVED -> state.received
        ConnectionsTab.SENT -> state.sent
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = AppTexts.CONNECTIONS_TITLE, onBack = onBack)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            MaterialChip(
                label = "${AppTexts.TAB_CONNECTED} (${state.connected.size})",
                selected = state.tab == ConnectionsTab.CONNECTED,
                onClick = { onTabChange(ConnectionsTab.CONNECTED) }
            )
            MaterialChip(
                label = "${AppTexts.TAB_RECEIVED} (${state.received.size})",
                selected = state.tab == ConnectionsTab.RECEIVED,
                onClick = { onTabChange(ConnectionsTab.RECEIVED) }
            )
            MaterialChip(
                label = "${AppTexts.TAB_SENT} (${state.sent.size})",
                selected = state.tab == ConnectionsTab.SENT,
                onClick = { onTabChange(ConnectionsTab.SENT) }
            )
        }

        if (!state.isLoading && items.isEmpty()) {
            when (state.tab) {
                ConnectionsTab.CONNECTED -> EmptyState(
                    message = AppTexts.EMPTY_CONNECTED,
                    hint = AppTexts.EMPTY_CONNECTED_HINT
                )
                ConnectionsTab.RECEIVED -> EmptyState(
                    message = AppTexts.EMPTY_RECEIVED,
                    hint = AppTexts.EMPTY_RECEIVED_HINT
                )
                ConnectionsTab.SENT -> EmptyState(
                    message = AppTexts.EMPTY_SENT,
                    hint = AppTexts.EMPTY_SENT_HINT
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).screenPadding()) {
                items(items, key = { it.connectionId }) { item ->
                    UserRow(
                        user = item.user,
                        subtitle = when (state.tab) {
                            ConnectionsTab.CONNECTED -> describeUser(item.user)
                            ConnectionsTab.RECEIVED -> formatRelativeTime(item.timestamp)
                            ConnectionsTab.SENT ->
                                "${AppTexts.SENT_PREFIX} · ${formatRelativeTime(item.timestamp)}"
                        },
                        onClick = { onUserClick(item.user.id) },
                        trailing = {
                            when (state.tab) {
                                ConnectionsTab.CONNECTED -> IconButton(
                                    onClick = { connectionToRemove = item.connectionId }
                                ) {
                                    Icon(
                                        Icons.Filled.PersonRemove,
                                        contentDescription = AppTexts.REMOVE_CONNECTION,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                ConnectionsTab.RECEIVED -> Row {
                                    IconButton(onClick = { onAccept(item.connectionId) }) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = AppTexts.ACCEPT,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { onReject(item.connectionId) }) {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = AppTexts.REJECT,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                ConnectionsTab.SENT -> TextButton(
                                    onClick = { onCancelSent(item.connectionId) }
                                ) { Text(AppTexts.CANCEL) }
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

    connectionToRemove?.let { id ->
        val name = state.connected.firstOrNull { it.connectionId == id }?.user?.name.orEmpty()
        AlertDialog(
            onDismissRequest = { connectionToRemove = null },
            title = { Text(AppTexts.REMOVE_CONNECTION_TITLE) },
            text = { Text(AppTexts.REMOVE_CONNECTION_MESSAGE.format(name)) },
            confirmButton = {
                TextButton(onClick = {
                    connectionToRemove = null
                    onRemove(id)
                }) { Text(AppTexts.REMOVE_CONNECTION_CONFIRM) }
            },
            dismissButton = {
                TextButton(onClick = { connectionToRemove = null }) {
                    Text(AppTexts.CANCEL_DIALOG_DISMISS)
                }
            }
        )
    }
}