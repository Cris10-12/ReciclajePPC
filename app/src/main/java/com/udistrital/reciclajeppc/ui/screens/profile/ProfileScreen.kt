package com.udistrital.reciclajeppc.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.cardPadding
import com.udistrital.reciclajeppc.core.util.getInitials
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.InfoRow
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.SecondaryButton

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onHistory: () -> Unit,
    onChangeRole: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val user = state.user

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = AppTexts.PROFILE_TITLE)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .screenPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Spacer(Modifier.height(Dimens.spaceXs))

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getInitials(user?.name.orEmpty()),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = user?.name.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

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
                    InfoRow(Icons.Filled.Phone, AppTexts.PROFILE_PHONE, user?.phone.orEmpty())
                    InfoRow(
                        Icons.Filled.LocationOn,
                        AppTexts.PROFILE_NEIGHBORHOOD,
                        user?.neighborhood.orEmpty()
                    )
                    InfoRow(
                        Icons.Filled.Recycling,
                        AppTexts.PROFILE_ACTIVE_ROLE,
                        state.role.label
                    )
                }
            }

            SecondaryButton(
                text = AppTexts.SEE_HISTORY,
                onClick = onHistory,
                icon = Icons.Filled.History
            )
            PrimaryButton(
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