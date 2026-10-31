package com.udistrital.reciclajeppc.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.ui.components.AppTextField
import com.udistrital.reciclajeppc.ui.components.BrandHeader
import com.udistrital.reciclajeppc.ui.components.LogoBadge
import com.udistrital.reciclajeppc.ui.components.PrimaryButton
import com.udistrital.reciclajeppc.ui.components.SecondaryButton

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado verde con el logo
        // Encabezado verde con el logo
        BrandHeader {
            LogoBadge()
            Spacer(Modifier.height(Dimens.spaceMd))
            Text(
                text = AppTexts.APP_NAME,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(Dimens.spaceXs))
            Text(
                text = AppTexts.TAGLINE,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
        // Formulario
        Column(
            modifier = Modifier.screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Spacer(Modifier.height(Dimens.spaceSm))
            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true
            )
            Spacer(Modifier.height(Dimens.spaceXs))
            PrimaryButton(
                text = AppTexts.LOGIN,
                onClick = { onLogin(email, password) }
            )
            SecondaryButton(
                text = AppTexts.CREATE_ACCOUNT,
                onClick = onGoToRegister
            )
            Text(
                text = "Modo demostración: puedes entrar con cualquier correo y contraseña.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}