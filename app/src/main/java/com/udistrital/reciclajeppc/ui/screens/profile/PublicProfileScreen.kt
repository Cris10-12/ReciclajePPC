package com.udistrital.reciclajeppc.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.cardPadding
import com.udistrital.reciclajeppc.core.util.formatRelativeTime
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.SecondaryButton
import com.udistrital.reciclajeppc.ui.components.SectionHeader
import com.udistrital.reciclajeppc.ui.components.StatCard
import com.udistrital.reciclajeppc.ui.components.UserAvatar


@Composable
fun PublicProfileScreen(
    state: PublicProfileUiState,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onConnect: () -> Unit,
    onGallery: () -> Unit,
    onConnectionsClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onRemoveConnection: () -> Unit,
    onRecommend: () -> Unit,
    onMessage: () -> Unit,
    onCloseTrail: (() -> Unit)? = null,
) {
    val user = state.user

    var showRemoveDialog by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = AppTexts.PROFILE_TITLE,
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
                if (user != null) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (state.isFavorite) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = if (state.isFavorite) AppTexts.REMOVE_FAVORITE
                            else AppTexts.ADD_FAVORITE
                        )
                    }
                }
            }
        )

        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            user == null -> EmptyState(
                message = AppTexts.PUBLIC_NOT_FOUND,
                hint = AppTexts.PUBLIC_NOT_FOUND_HINT
            )

            else -> Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .screenPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
            ) {
                Spacer(Modifier.height(Dimens.spaceXs))

                UserAvatar(
                    name = user.name,
                    avatarUri = user.avatarUri,
                    size = 96.dp,
                    textStyle = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (user.neighborhood.isNotBlank()) {
                    Text(
                        text = user.neighborhood,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = user.roles.joinToString(" y ") { it.label },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // ---- Estado de la conexión ----
                when (state.relation) {
                    ConnectionRelation.NONE -> PrimaryButton(
                        text = AppTexts.CONNECT,
                        onClick = onConnect,
                        icon = Icons.Filled.PersonAdd
                    )
                    ConnectionRelation.REQUEST_SENT -> {
                        Text(
                            text = AppTexts.CONNECT_SENT_INFO,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        SecondaryButton(
                            text = AppTexts.CANCEL_CONNECT_REQUEST,
                            onClick = onRemoveConnection,
                            icon = Icons.Filled.Close
                        )
                    }
                    ConnectionRelation.REQUEST_RECEIVED -> {
                        Text(
                            text = AppTexts.CONNECT_RECEIVED,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)) {
                            PrimaryButton(
                                text = AppTexts.ACCEPT,
                                onClick = onAccept,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Filled.Check
                            )
                            SecondaryButton(
                                text = AppTexts.REJECT,
                                onClick = onReject,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    ConnectionRelation.CONNECTED -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(Dimens.spaceXs))
                            Text(
                                text = AppTexts.CONNECTED,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (!state.hasRecommendedByMe) {
                            PrimaryButton(
                                text = AppTexts.RECOMMEND,
                                onClick = onRecommend,
                                icon = Icons.Filled.ThumbUp
                            )
                        }
                        SecondaryButton(
                            text = AppTexts.REMOVE_CONNECTION,
                            onClick = { showRemoveDialog = true },
                            icon = Icons.Filled.PersonRemove
                        )
                    }
                }


                // ---- Mensaje ----
                if (state.canMessage) {
                    SecondaryButton(
                        text = AppTexts.SEND_MESSAGE,
                        onClick = onMessage,
                        icon = Icons.Filled.Chat
                    )
                    state.chatError?.let { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = AppTexts.CHAT_PROFILE_HINT,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }


                // ---- Sobre mí ----
                if (user.bio.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = Dimens.cardElevation
                        )
                    ) {
                        Column(
                            modifier = Modifier.cardPadding(),
                            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
                        ) {
                            Text(
                                text = AppTexts.PROFILE_ABOUT,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(text = user.bio, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                // ---- Contadores ----
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)) {
                    StatCard(
                        value = state.connectionCount.toString(),
                        label = AppTexts.STAT_CONNECTIONS,
                        icon = Icons.Filled.Group,
                        modifier = Modifier.weight(1f),
                        onClick = onConnectionsClick
                    )
                    StatCard(
                        value = state.recommendations.size.toString(),
                        label = AppTexts.STAT_RECOMMENDATIONS,
                        icon = Icons.Filled.ThumbUp,
                        modifier = Modifier.weight(1f)
                    )
                }

                // ---- Fotos ----
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
                ) {
                    if (state.photoCount > 0) {
                        SectionHeader(
                            title = "${AppTexts.PUBLIC_PHOTOS} (${state.photoCount})",
                            actionText = AppTexts.SEE_ALL,
                            onAction = onGallery
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
                        ) {
                            state.previewPhotos.forEach { photo ->
                                AsyncImage(
                                    model = photo.uri,
                                    contentDescription = photo.caption.ifBlank { null },
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .clickable(onClick = onGallery)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = AppTexts.PUBLIC_PHOTOS,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = AppTexts.PUBLIC_NO_PHOTOS,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ---- Recomendaciones ----
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
                ) {
                    Text(
                        text = AppTexts.PUBLIC_RECOMMENDATIONS,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (state.recommendations.isEmpty()) {
                        Text(
                            text = AppTexts.PUBLIC_NO_RECOMMENDATIONS,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.recommendations.forEach { item ->
                            RecommendationCard(
                                item = item,
                                onAuthorClick = onUserClick,
                                onEdit = onRecommend
                            )
                        }
                    }
                }

                Spacer(Modifier.height(Dimens.spaceMd))
            }
        }
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text(AppTexts.REMOVE_CONNECTION_TITLE) },
            text = {
                Text(AppTexts.REMOVE_CONNECTION_MESSAGE.format(user?.name.orEmpty()))
            },
            confirmButton = {
                TextButton(onClick = {
                    showRemoveDialog = false
                    onRemoveConnection()
                }) { Text(AppTexts.REMOVE_CONNECTION_CONFIRM) }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text(AppTexts.CANCEL_DIALOG_DISMISS)
                }
            }
        )
    }
}

@Composable
private fun RecommendationCard(
    item: RecommendationItem,
    onAuthorClick: (String) -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
    ) {
        Column(
            modifier = Modifier.cardPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAuthorClick(item.authorId) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        name = item.authorName,
                        avatarUri = item.authorAvatarUri,
                        size = 36.dp
                    )
                    Spacer(Modifier.width(Dimens.spaceSm))
                    Column {
                        Text(
                            text = item.authorName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = formatRelativeTime(item.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (item.isMine) {
                    TextButton(onClick = onEdit) { Text(AppTexts.RECOMMEND_EDIT) }
                }
            }
            Text(text = item.comment, style = MaterialTheme.typography.bodyMedium)
        }
    }
}