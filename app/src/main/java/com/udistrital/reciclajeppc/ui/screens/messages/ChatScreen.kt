package com.udistrital.reciclajeppc.ui.screens.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.formatClock
import com.udistrital.reciclajeppc.domain.model.ChatMessage
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.UserAvatar
import com.udistrital.reciclajeppc.ui.components.labels

@Composable
fun ChatScreen(
    state: ChatUiState,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onUserClick: (String) -> Unit,
    onRequestClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val conversation = state.conversation
    val listState = rememberLazyListState()
    val messageCount = conversation?.messages?.size ?: 0

    LaunchedEffect(messageCount) {
        if (messageCount > 0) listState.animateScrollToItem(messageCount - 1)
    }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        AppTopBar(
            title = state.contact?.name ?: AppTexts.CHAT_TITLE,
            onBack = onBack
        )

        if (conversation != null) {
            ChatContextBar(
                contact = state.contact,
                request = state.request,
                onUserClick = onUserClick,
                onRequestClick = onRequestClick
            )
        }

        when {
            state.isLoading -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            conversation == null -> Box(modifier = Modifier.weight(1f)) {
                EmptyState(
                    message = AppTexts.EMPTY_MESSAGES,
                    hint = AppTexts.EMPTY_MESSAGES_HINT
                )
            }

            conversation.messages.isEmpty() -> Box(modifier = Modifier.weight(1f)) {
                EmptyState(
                    message = AppTexts.CHAT_EMPTY,
                    hint = AppTexts.CHAT_EMPTY_HINT
                )
            }

            else -> LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(Dimens.spaceMd),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                items(conversation.messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        mine = message.senderId == state.currentUserId
                    )
                }
            }
        }

        if (conversation != null) {
            if (state.canWrite) {
                MessageInput(
                    value = state.draft,
                    canSend = state.canSend,
                    onValueChange = onDraftChange,
                    onSend = onSend
                )
            } else {
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                    Text(
                        text = AppTexts.CHAT_READ_ONLY,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(Dimens.spaceMd)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatContextBar(
    contact: User?,
    request: RecycleRequest?,
    onUserClick: (String) -> Unit,
    onRequestClick: (String) -> Unit
) {
    val name = contact?.name ?: AppTexts.UNKNOWN_USER

    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (contact != null) Modifier.clickable { onUserClick(contact.id) }
                        else Modifier
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(name = name, avatarUri = contact?.avatarUri, size = 40.dp)
                Spacer(Modifier.width(Dimens.spaceSm))
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (request != null) {
                        Text(
                            text = "${request.materials.labels()} · ${request.neighborhood}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (request != null) {
                TextButton(onClick = { onRequestClick(request.id) }) {
                    Text(AppTexts.CHAT_VIEW_REQUEST)
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, mine: Boolean) {
    val shape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (mine) 18.dp else 4.dp,
        bottomEnd = if (mine) 4.dp else 18.dp
    )
    val container = if (mine) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.primaryContainer
    val content = if (mine) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onPrimaryContainer

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (mine) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = shape,
            color = container,
            contentColor = content,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(text = message.text, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = formatClock(message.sentAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = content.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun MessageInput(
    value: String,
    canSend: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spaceSm, vertical = Dimens.spaceSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(AppTexts.CHAT_HINT) },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() })
            )
            IconButton(
                onClick = onSend,
                enabled = canSend,
                modifier = Modifier.padding(start = Dimens.spaceXs),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = AppTexts.CHAT_SEND)
            }
        }
    }
}