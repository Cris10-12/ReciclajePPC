package com.udistrital.reciclajeppc.ui.screens.recycler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.RequestCard
import com.udistrital.reciclajeppc.core.util.pluralize


@Composable
fun AgendaScreen(
    state: AgendaUiState,
    onExplore: () -> Unit,
    onRequestClick: (String) -> Unit,
    onHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = AppTexts.AGENDA_TITLE,
            actions = {
                IconButton(onClick = onHistory) {
                    Icon(Icons.Filled.History, contentDescription = AppTexts.SEE_HISTORY)
                }
            }
        )

        Column(
            modifier = Modifier.screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
        ) {
            PrimaryButton(
                text = AppTexts.EXPLORE_REQUESTS,
                onClick = onExplore,
                icon = Icons.Filled.Search
            )
            if (state.requests.isNotEmpty()) {
                val n = state.requests.size
                Text(
                    text = pluralize(
                        state.requests.size,
                        "recolección pendiente",
                        "recolecciones pendientes"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!state.isLoading && state.requests.isEmpty()) {
            EmptyState(
                message = AppTexts.EMPTY_AGENDA,
                hint = AppTexts.EMPTY_AGENDA_HINT
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