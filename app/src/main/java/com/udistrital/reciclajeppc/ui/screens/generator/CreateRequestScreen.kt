package com.udistrital.reciclajeppc.ui.screens.generator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.domain.model.MaterialType
import com.udistrital.reciclajeppc.ui.components.AppTextField
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.MaterialChip
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.icon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateRequestScreen(
    state: CreateRequestUiState,
    onMaterialToggle: (MaterialType) -> Unit,
    onWeightChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onAvailabilityChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onPublished: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(state.isPublished) {
        if (state.isPublished) onPublished()
    }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        AppTopBar(title = AppTexts.CREATE_TITLE, onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Spacer(Modifier.height(Dimens.spaceXs))

            Text(
                text = AppTexts.FIELD_MATERIAL,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                MaterialType.entries.forEach { type ->
                    MaterialChip(
                        label = type.label,
                        selected = type in state.materials,
                        onClick = { onMaterialToggle(type) },
                        icon = type.icon()
                    )
                }
            }
            state.materialError?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            AppTextField(
                value = state.weight,
                onValueChange = onWeightChange,
                label = AppTexts.FIELD_WEIGHT,
                keyboardType = KeyboardType.Decimal,
                errorText = state.weightError
            )
            AppTextField(
                value = state.address,
                onValueChange = onAddressChange,
                label = AppTexts.FIELD_ADDRESS,
                errorText = state.addressError
            )
            AppTextField(
                value = state.neighborhood,
                onValueChange = onNeighborhoodChange,
                label = AppTexts.FIELD_NEIGHBORHOOD,
                errorText = state.neighborhoodError
            )
            AppTextField(
                value = state.availability,
                onValueChange = onAvailabilityChange,
                label = AppTexts.FIELD_AVAILABILITY,
                errorText = state.availabilityError
            )
            AppTextField(
                value = state.notes,
                onValueChange = onNotesChange,
                label = AppTexts.FIELD_NOTES,
                singleLine = false,
                minLines = 3
            )

            PrimaryButton(
                text = if (state.isSubmitting) AppTexts.PUBLISHING else AppTexts.PUBLISH_REQUEST,
                onClick = onSubmit,
                enabled = !state.isSubmitting
            )
            Spacer(Modifier.height(Dimens.spaceMd))
        }
    }
}