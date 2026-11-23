package com.udistrital.reciclajeppc.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTextField
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.SecondaryButton
import com.udistrital.reciclajeppc.ui.components.UserAvatar

@Composable
fun EditProfileScreen(
    state: EditProfileUiState,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onAvatarChange: (String?) -> Unit,
    onSave: () -> Unit,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onAvatarChange(uri.toString())
    }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        AppTopBar(title = AppTexts.EDIT_PROFILE_TITLE, onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Spacer(Modifier.height(Dimens.spaceXs))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                UserAvatar(
                    name = state.name,
                    avatarUri = state.avatarUri,
                    size = 96.dp,
                    textStyle = MaterialTheme.typography.headlineMedium
                )
            }
            SecondaryButton(
                text = if (state.avatarUri == null) AppTexts.ADD_PHOTO else AppTexts.CHANGE_PHOTO,
                onClick = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                icon = Icons.Filled.PhotoCamera
            )
            if (state.avatarUri != null) {
                SecondaryButton(
                    text = AppTexts.REMOVE_PHOTO,
                    onClick = { onAvatarChange(null) },
                    icon = Icons.Filled.Delete
                )
            }

            AppTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = AppTexts.FIELD_NAME,
                leadingIcon = Icons.Filled.Person,
                errorText = state.nameError
            )
            AppTextField(
                value = state.phone,
                onValueChange = onPhoneChange,
                label = AppTexts.FIELD_PHONE,
                leadingIcon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone,
                errorText = state.phoneError
            )
            AppTextField(
                value = state.neighborhood,
                onValueChange = onNeighborhoodChange,
                label = AppTexts.FIELD_NEIGHBORHOOD,
                leadingIcon = Icons.Filled.LocationOn
            )
            AppTextField(
                value = state.bio,
                onValueChange = onBioChange,
                label = AppTexts.FIELD_BIO,
                leadingIcon = Icons.Filled.Info,
                singleLine = false,
                minLines = 3
            )
            Text(
                text = "${state.bio.length}/${EditProfileViewModel.BIO_MAX}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = AppTexts.EDIT_LOCKED_NOTE,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.saveError != null) {
                Text(
                    text = state.saveError,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            PrimaryButton(
                text = if (state.isSaving) AppTexts.SAVING else AppTexts.SAVE_CHANGES,
                onClick = onSave,
                enabled = !state.isSaving
            )
            Spacer(Modifier.height(Dimens.spaceMd))
        }
    }
}