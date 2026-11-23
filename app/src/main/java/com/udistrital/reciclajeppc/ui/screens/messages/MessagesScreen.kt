package com.udistrital.reciclajeppc.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.formatRelativeTime
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.UserAvatar
import com.udistrital.reciclajeppc.ui.components.labels

@Composable
fun MessagesScreen(
    state: MessagesUiState,
    onConversationClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = AppTexts.MESSAGES_TITLE, onBack = onBack)

        when {
            state.isLoading -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.conversations.isEmpty() -> EmptyState(
                message = AppTexts.MESSAGES_EMPTY,
                hint = AppTexts.MESSAGES_EMPTY_HINT
            )

            else -> LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.conversations, key = { it.id }) { item ->
                    ConversationRow(item = item, onClick = { onConversationClick(item.id) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ConversationRow(item: ConversationItem, onClick: () -> Unit) {
    val name = item.contact?.name ?: AppTexts.UNKNOWN_USER

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm + 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(name = name, avatarUri = item.contact?.avatarUri, size = 48.dp)

        Spacer(Modifier.width(Dimens.spaceMd))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (item.unread > 0) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            item.request?.let { request ->
                Text(
                    text = request.materials.labels(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = item.lastMessage.ifBlank { AppTexts.CHAT_EMPTY },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(Dimens.spaceSm))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item.lastMessageAt?.let { sentAt ->
                Text(
                    text = formatRelativeTime(sentAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (item.unread > 0) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.unread.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}