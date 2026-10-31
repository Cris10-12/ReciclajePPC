package com.udistrital.reciclajeppc.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.cardPadding
import com.udistrital.reciclajeppc.core.util.formatWeight
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.SecondaryButton
import com.udistrital.reciclajeppc.ui.components.StatusBadge
import com.udistrital.reciclajeppc.ui.components.icon
import com.udistrital.reciclajeppc.ui.components.labels
import com.udistrital.reciclajeppc.ui.components.InfoRow


@Composable
fun RequestDetailScreen(
    state: RequestDetailUiState,
    onAccept: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = AppTexts.DETAIL_TITLE, onBack = onBack)

        val request = state.request
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            request == null -> EmptyState(
                message = AppTexts.DETAIL_NOT_FOUND,
                hint = AppTexts.DETAIL_NOT_FOUND_HINT
            )

            else -> Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .screenPadding(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
            ) {
                Spacer(Modifier.height(Dimens.spaceXs))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = request.material.icon(),
                            contentDescription = request.material.label,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.iconLg)
                        )
                    }
                    Spacer(Modifier.width(Dimens.spaceMd))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = request.materials.labels(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatWeight(request.weightKg),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(request.status)
                }

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
                        InfoRow(Icons.Filled.Person, "Generador", request.generatorName)
                        InfoRow(Icons.Filled.LocationOn, "Dirección", request.address)
                        InfoRow(Icons.Filled.LocationOn, "Barrio", request.neighborhood)
                        InfoRow(Icons.Filled.Schedule, "Disponibilidad", request.availability)
                        InfoRow(
                            Icons.Filled.Description,
                            "Notas",
                            request.notes.ifBlank { AppTexts.DETAIL_NO_NOTES }
                        )
                        request.recyclerName?.let {
                            InfoRow(Icons.Filled.Recycling, "Reciclador", it)
                        }
                    }
                }

                if (state.canAccept) {
                    PrimaryButton(
                        text = AppTexts.ACCEPT_REQUEST,
                        onClick = onAccept,
                        enabled = !state.isWorking
                    )
                }
                if (state.canComplete) {
                    PrimaryButton(
                        text = AppTexts.COMPLETE_REQUEST,
                        onClick = onComplete,
                        enabled = !state.isWorking
                    )
                }
                if (state.canCancel) {
                    SecondaryButton(
                        text = AppTexts.CANCEL_REQUEST,
                        onClick = { showCancelDialog = true },
                        enabled = !state.isWorking
                    )
                }
                Spacer(Modifier.height(Dimens.spaceMd))
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text(AppTexts.CANCEL_DIALOG_TITLE) },
            text = { Text(AppTexts.CANCEL_DIALOG_MESSAGE) },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    onCancel()
                }) { Text(AppTexts.CANCEL_DIALOG_CONFIRM) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(AppTexts.CANCEL_DIALOG_DISMISS)
                }
            }
        )
    }
}
