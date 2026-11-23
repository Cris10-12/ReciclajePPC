package com.udistrital.reciclajeppc.ui.screens.recommendations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.domain.model.Recommendation
import com.udistrital.reciclajeppc.ui.components.AppTextField
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.SecondaryButton
import com.udistrital.reciclajeppc.ui.components.UserAvatar

@Composable
fun RecommendScreen(
    state: RecommendUiState,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onRemove: () -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    var showRemoveDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.done) {
        if (state.done) onDone()
    }

    val recipient = state.recipient
    val isEditing = state.existing != null

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = if (isEditing) AppTexts.RECOMMEND_EDIT_TITLE else AppTexts.RECOMMEND_TITLE,
            onBack = onBack,
            actions = {}
        )

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            recipient == null -> EmptyState(
                message = AppTexts.PUBLIC_NOT_FOUND,
                hint = AppTexts.PUBLIC_NOT_FOUND_HINT
            )

            state.isSelf -> EmptyState(
                message = AppTexts.RECOMMEND_SELF,
                hint = AppTexts.RECOMMEND_SELF_HINT
            )

            // Quien ya recomendó puede editar o retirar aunque la conexión se haya eliminado.
            !isEditing && !state.canRecommend -> EmptyState(
                message = AppTexts.RECOMMEND_NOT_CONNECTED,
                hint = AppTexts.RECOMMEND_NOT_CONNECTED_HINT
            )

            else -> Column(
                modifier = Modifier
                    .weight(1f)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .screenPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
            ) {
                Spacer(Modifier.height(Dimens.spaceXs))

                UserAvatar(
                    name = recipient.name,
                    avatarUri = recipient.avatarUri,
                    size = 72.dp,
                    textStyle = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = recipient.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = AppTexts.RECOMMEND_INTRO,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                AppTextField(
                    value = state.comment,
                    onValueChange = onCommentChange,
                    label = AppTexts.RECOMMEND_FIELD,
                    singleLine = false,
                    minLines = 4,
                    errorText = state.commentError,
                    helperText = AppTexts.RECOMMEND_COUNTER.format(
                        state.comment.length,
                        Recommendation.MAX_LENGTH
                    )
                )

                state.submitError?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                PrimaryButton(
                    text = when {
                        state.isSaving -> AppTexts.SAVING
                        isEditing -> AppTexts.SAVE_CHANGES
                        else -> AppTexts.RECOMMEND_SEND
                    },
                    onClick = onSubmit,
                    enabled = !state.isSaving,
                    icon = Icons.Filled.ThumbUp
                )

                if (isEditing) {
                    SecondaryButton(
                        text = AppTexts.RECOMMEND_REMOVE,
                        onClick = { showRemoveDialog = true },
                        enabled = !state.isSaving,
                        icon = Icons.Filled.Delete
                    )
                }

                Spacer(Modifier.height(Dimens.spaceMd))
            }
        }
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text(AppTexts.RECOMMEND_REMOVE_TITLE) },
            text = {
                Text(AppTexts.RECOMMEND_REMOVE_MESSAGE.format(recipient?.name.orEmpty()))
            },
            confirmButton = {
                TextButton(onClick = {
                    showRemoveDialog = false
                    onRemove()
                }) { Text(AppTexts.RECOMMEND_REMOVE_CONFIRM) }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text(AppTexts.CANCEL_DIALOG_DISMISS)
                }
            }
        )
    }
}