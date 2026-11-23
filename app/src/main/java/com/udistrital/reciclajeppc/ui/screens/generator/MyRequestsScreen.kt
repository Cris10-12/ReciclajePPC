package com.udistrital.reciclajeppc.ui.screens.generator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.MaterialChip
import com.udistrital.reciclajeppc.ui.components.MessagesAction
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.RequestCard

@Composable
fun MyRequestsScreen(
    state: MyRequestsUiState,
    onFilterChange: (RequestStatus?) -> Unit,
    onCreateRequest: () -> Unit,
    onRequestClick: (String) -> Unit,
    onHistory: () -> Unit,
    unreadMessages: Int,
    onMessages: () -> Unit,
    onUserClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(
            title = AppTexts.MY_REQUESTS_TITLE,
            actions = {
                IconButton(onClick = onHistory) {
                    Icon(Icons.Filled.History, contentDescription = AppTexts.SEE_HISTORY)
                }
                MessagesAction(unreadCount = unreadMessages, onClick = onMessages)
            }
        )

        Column(
            modifier = Modifier.screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            PrimaryButton(
                text = AppTexts.PUBLISH_REQUEST,
                onClick = onCreateRequest,
                icon = Icons.Filled.Add
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)) {
                MaterialChip(
                    label = AppTexts.FILTER_ALL,
                    selected = state.filter == null,
                    onClick = { onFilterChange(null) }
                )
                listOf(RequestStatus.AVAILABLE, RequestStatus.ACCEPTED).forEach { status ->
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
                    message = AppTexts.EMPTY_REQUESTS,
                    hint = AppTexts.EMPTY_REQUESTS_HINT
                )
            } else {
                EmptyState(
                    message = AppTexts.EMPTY_REQUESTS_FILTER,
                    hint = AppTexts.EMPTY_REQUESTS_FILTER_HINT
                )            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).screenPadding(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
            ) {
                items(state.requests, key = { it.id }) { request ->
                    RequestCard(
                        request = request,
                        onClick = { onRequestClick(request.id) },
                        person = request.recyclerId?.let { state.users[it] },
                        onPersonClick = onUserClick
                    )
                }
            }
        }
    }
}