package com.udistrital.reciclajeppc.ui.screens.recycler

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.domain.model.MaterialType
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.MaterialChip
import com.udistrital.reciclajeppc.ui.components.RequestCard
import com.udistrital.reciclajeppc.ui.components.icon
import com.udistrital.reciclajeppc.core.util.pluralize


@Composable
fun ExploreScreen(
    state: ExploreUiState,
    onMaterialToggle: (MaterialType) -> Unit,
    onClearFilter: () -> Unit,
    onRequestClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = AppTexts.EXPLORE_REQUESTS)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            MaterialChip(
                label = AppTexts.EXPLORE_ALL,
                selected = state.selectedMaterials.isEmpty(),
                onClick = onClearFilter
            )
            MaterialType.entries.forEach { type ->
                MaterialChip(
                    label = type.label,
                    selected = type in state.selectedMaterials,
                    onClick = { onMaterialToggle(type) },
                    icon = type.icon()
                )
            }
        }

        Text(
            text = pluralize(state.requests.size, "solicitud disponible", "solicitudes disponibles"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = Dimens.spaceMd)
        )

        if (state.requests.isEmpty()) {
            EmptyState(
                message = AppTexts.EXPLORE_EMPTY,
                hint = AppTexts.EXPLORE_EMPTY_HINT
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).screenPadding(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
            ) {
                items(state.requests, key = { it.id }) { request ->
                    RequestCard(request = request, onClick = { onRequestClick(request.id) })
                }
            }
        }
    }
}