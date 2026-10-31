package com.udistrital.reciclajeppc.ui.screens.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.domain.model.ChatMessage
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState


@Composable
fun ChatScreen(
    state: ChatUiState,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit
) {
    val conversation = state.conversation
    val listState = rememberLazyListState()
    val messageCount = conversation?.messages?.size ?: 0

    // Baja al último mensaje al abrir el chat y cada vez que llega uno nuevo.
    LaunchedEffect(messageCount) {
        if (messageCount > 0) listState.animateScrollToItem(messageCount - 1)
    }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        AppTopBar(
            title = conversation?.contactName ?: AppTexts.CHAT_TITLE,
            onBack = onBack
        )

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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Dimens.spaceMd),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                items(conversation.messages, key = { it.id }) { message ->
                    MessageBubble(message)
                }
            }
        }

        if (conversation != null) {
            MessageInput(
                value = state.draft,
                canSend = state.canSend,
                onValueChange = onDraftChange,
                onSend = onSend
            )
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val mine = message.isMine
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
                    text = message.time,
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