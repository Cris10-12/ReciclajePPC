package com.udistrital.reciclajeppc.ui.screens.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.formatWeight
import com.udistrital.reciclajeppc.core.util.pluralize
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.model.UserRole
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.MaterialChip
import com.udistrital.reciclajeppc.ui.components.RequestCard


@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onFilterChange: (RequestStatus?) -> Unit,
    onRequestClick: (String) -> Unit,
    onBack: () -> Unit,
    onUserClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = AppTexts.HISTORY_TITLE, onBack = onBack)

        Column(
            modifier = Modifier.screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            Text(
                text = "Modo ${state.role.label} · " +
                        "${pluralize(state.completedCount, "completada", "completadas")} · " +
                        formatWeight(state.completedKg),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)) {
                MaterialChip(
                    label = AppTexts.FILTER_ALL,
                    selected = state.filter == null,
                    onClick = { onFilterChange(null) }
                )
                listOf(RequestStatus.COMPLETED, RequestStatus.CANCELLED).forEach { status ->
                    MaterialChip(
                        label = status.label,
                        selected = state.filter == status,
                        onClick = { onFilterChange(status) }
                    )
                }
            }
        }

        if (!state.isLoading && state.requests.isEmpty()) {
            if (state.filter == null) {
                EmptyState(
                    message = AppTexts.EMPTY_HISTORY,
                    hint = AppTexts.EMPTY_HISTORY_HINT
                )
            } else {
                EmptyState(
                    message = AppTexts.EMPTY_HISTORY_FILTER,
                    hint = AppTexts.EMPTY_HISTORY_FILTER_HINT
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).screenPadding(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
            ) {
                items(state.requests, key = { it.id }) { request ->
                    RequestCard(
                        request = request,
                        onClick = { onRequestClick(request.id) },
                        person = if (state.role == UserRole.GENERATOR) {
                            request.recyclerId?.let { state.users[it] }
                        } else {
                            state.users[request.generatorId]
                        },
                        onPersonClick = onUserClick
                    )
                }
            }
        }
    }
}