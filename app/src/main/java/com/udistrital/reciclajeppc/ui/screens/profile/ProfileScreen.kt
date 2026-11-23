package com.udistrital.reciclajeppc.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.cardPadding
import com.udistrital.reciclajeppc.core.util.pluralize
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.InfoRow
import com.udistrital.reciclajeppc.ui.components.MessagesAction
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.SecondaryButton
import com.udistrital.reciclajeppc.ui.components.StatCard
import com.udistrital.reciclajeppc.ui.components.UserAvatar


@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onEdit: () -> Unit,
    onGallery: () -> Unit,
    onConnections: () -> Unit,
    onFavorites: () -> Unit,
    onHistory: () -> Unit,
    onChangeRole: () -> Unit,
    onLogout: () -> Unit,
    unreadMessages: Int,
    onMessages: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val user = state.user

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = AppTexts.PROFILE_TITLE,
            actions = {
                MessagesAction(unreadCount = unreadMessages, onClick = onMessages)
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .screenPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Spacer(Modifier.height(Dimens.spaceXs))

            UserAvatar(
                name = user?.name.orEmpty(),
                avatarUri = user?.avatarUri,
                size = 96.dp,
                textStyle = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = user?.name.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (!user?.neighborhood.isNullOrBlank()) {
                Text(
                    text = user?.neighborhood.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ---- Sobre mí ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
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
                    val bio = user?.bio.orEmpty()
                    Text(
                        text = bio.ifBlank { AppTexts.PROFILE_BIO_EMPTY },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (bio.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // ---- Contadores (calculados, no guardados) ----
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)) {
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
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)) {
                StatCard(
                    value = state.recommendationCount.toString(),
                    label = AppTexts.STAT_RECOMMENDATIONS,
                    icon = Icons.Filled.ThumbUp,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = state.photoCount.toString(),
                    label = AppTexts.STAT_PHOTOS,
                    icon = Icons.Filled.PhotoLibrary,
                    modifier = Modifier.weight(1f),
                    onClick = onGallery
                )
            }

            // ---- Datos personales (el documento solo se ve en el perfil propio) ----
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
            ) {
                Column(
                    modifier = Modifier.cardPadding(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
                ) {
                    InfoRow(Icons.Filled.Email, AppTexts.PROFILE_EMAIL, user?.email.orEmpty())
                    InfoRow(
                        Icons.Filled.Badge,
                        AppTexts.PROFILE_DOCUMENT,
                        user?.let { "${it.documentType.shortLabel} ${it.documentNumber}" }.orEmpty()
                    )
                    InfoRow(
                        Icons.Filled.Phone,
                        AppTexts.PROFILE_PHONE,
                        user?.phone.orEmpty().ifBlank { AppTexts.NOT_SET }
                    )
                    InfoRow(
                        Icons.Filled.LocationOn,
                        AppTexts.PROFILE_NEIGHBORHOOD,
                        user?.neighborhood.orEmpty().ifBlank { AppTexts.NOT_SET }
                    )
                    InfoRow(
                        Icons.Filled.Recycling,
                        AppTexts.PROFILE_ROLES,
                        user?.roles?.joinToString(" y ") { it.label }.orEmpty()
                    )
                    InfoRow(
                        Icons.Filled.Recycling,
                        AppTexts.PROFILE_ACTIVE_ROLE,
                        state.role.label
                    )
                }
            }

            PrimaryButton(
                text = AppTexts.EDIT_PROFILE,
                onClick = onEdit,
                icon = Icons.Filled.Edit
            )
            SecondaryButton(
                text = AppTexts.MY_GALLERY,
                onClick = onGallery,
                icon = Icons.Filled.PhotoLibrary
            )
            SecondaryButton(
                text = if (state.pendingRequestCount > 0) {
                    "${AppTexts.MY_CONNECTIONS} · " +
                            pluralize(state.pendingRequestCount, "solicitud nueva", "solicitudes nuevas")
                } else {
                    AppTexts.MY_CONNECTIONS
                },
                onClick = onConnections,
                icon = Icons.Filled.Group
            )
            SecondaryButton(
                text = AppTexts.SEE_HISTORY,
                onClick = onHistory,
                icon = Icons.Filled.History
            )
            SecondaryButton(
                text = AppTexts.CHANGE_ROLE,
                onClick = onChangeRole,
                icon = Icons.Filled.SwapHoriz
            )
            SecondaryButton(
                text = AppTexts.LOGOUT,
                onClick = { showLogoutDialog = true },
                icon = Icons.Filled.Logout
            )
            Spacer(Modifier.height(Dimens.spaceMd))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(AppTexts.LOGOUT_DIALOG_TITLE) },
            text = { Text(AppTexts.LOGOUT_DIALOG_MESSAGE) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) { Text(AppTexts.LOGOUT_DIALOG_CONFIRM) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(AppTexts.LOGOUT_DIALOG_DISMISS)
                }
            }
        )
    }
}