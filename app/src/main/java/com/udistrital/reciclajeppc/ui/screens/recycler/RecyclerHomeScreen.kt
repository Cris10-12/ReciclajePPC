package com.udistrital.reciclajeppc.ui.screens.recycler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
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
fun RecyclerHomeScreen(
    state: RecyclerHomeUiState,
    onExplore: () -> Unit,
    onRequestClick: (String) -> Unit,
    onSeeAgenda: () -> Unit
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
                text = "Modo Reciclador · esto es lo que has recolectado",
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
                    value = state.acceptedRequests.size.toString(),
                    label = "Por recoger",
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
                    value = formatWeight(state.collectedKg),
                    label = "Recolectado",
                    icon = Icons.Filled.Recycling,
                    modifier = Modifier.weight(1f)
                )
            }

            PrimaryButton(
                text = AppTexts.EXPLORE_REQUESTS,
                onClick = onExplore,
                icon = Icons.Filled.Search
            )

            SectionHeader(
                title = "Mi agenda",
                actionText = "Ver agenda",
                onAction = onSeeAgenda
            )
            if (state.acceptedRequests.isEmpty()) {
                EmptyState(
                    message = "No tienes recolecciones pendientes",
                    hint = "Acepta una solicitud y aparecerá aquí."
                )
            } else {
                state.acceptedRequests.take(3).forEach { request ->
                    RequestCard(request = request, onClick = { onRequestClick(request.id) })
                }
            }

            SectionHeader(
                title = "Solicitudes disponibles",
                actionText = "Ver todas",
                onAction = onExplore
            )
            if (state.availableRequests.isEmpty()) {
                EmptyState(
                    message = "No hay solicitudes disponibles",
                    hint = "Vuelve más tarde, los generadores publican todo el tiempo."
                )
            } else {
                state.availableRequests.take(3).forEach { request ->
                    RequestCard(request = request, onClick = { onRequestClick(request.id) })
                }
            }
        }
    }
}