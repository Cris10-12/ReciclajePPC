package com.udistrital.reciclajeppc.ui.screens.generator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.formatWeight
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.BrandHeader
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.RequestCard
import com.udistrital.reciclajeppc.ui.components.SectionHeader
import com.udistrital.reciclajeppc.ui.components.StatCard

@Composable
fun GeneratorHomeScreen(
    state: GeneratorHomeUiState,
    onCreateRequest: () -> Unit,
    onRequestClick: (String) -> Unit,
    onSeeAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        BrandHeader(verticalPadding = 28.dp) {
            Text(
                text = if (state.userName.isBlank()) "¡Hola!" else "¡Hola, ${state.userName}!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "Modo Generador · esto es lo que has reciclado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
            )
        }

        Column(
            modifier = Modifier.screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Spacer(Modifier.height(Dimens.spaceXs))

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)) {
                StatCard(
                    value = state.activeRequests.size.toString(),
                    label = "Activas",
                    icon = Icons.Filled.Schedule,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = state.completedCount.toString(),
                    label = "Completadas",
                    icon = Icons.Filled.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = formatWeight(state.recycledKg),
                    label = "Reciclado",
                    icon = Icons.Filled.Recycling,
                    modifier = Modifier.weight(1f)
                )
            }

            PrimaryButton(
                text = AppTexts.PUBLISH_REQUEST,
                onClick = onCreateRequest,
                icon = Icons.Filled.Add
            )

            SectionHeader(
                title = "Solicitudes activas",
                actionText = "Ver todas",
                onAction = onSeeAll
            )

            if (state.activeRequests.isEmpty()) {
                EmptyState(
                    message = "No tienes solicitudes activas",
                    hint = "Publica tu primera solicitud y un reciclador la verá."
                )
            } else {
                state.activeRequests.take(3).forEach { request ->
                    RequestCard(
                        request = request,
                        onClick = { onRequestClick(request.id) }
                    )
                }
            }
        }
    }
}