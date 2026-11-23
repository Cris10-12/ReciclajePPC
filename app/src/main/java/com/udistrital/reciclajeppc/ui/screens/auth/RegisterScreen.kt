package com.udistrital.reciclajeppc.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.cardPadding
import com.udistrital.reciclajeppc.core.util.sanitizeDocument
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.core.util.validateDocument
import com.udistrital.reciclajeppc.domain.model.DocumentType
import com.udistrital.reciclajeppc.ui.components.AppTextField
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.MaterialChip
import com.udistrital.reciclajeppc.ui.components.PrimaryButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    onRegister: (
        name: String,
        email: String,
        documentType: DocumentType,
        documentNumber: String,
        phone: String,
        neighborhood: String,
        password: String
    ) -> Unit,
    onBack: () -> Unit,
    submitError: String? = null
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var documentType by rememberSaveable { mutableStateOf(DocumentType.CC) }
    var documentNumber by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var neighborhood by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    val nameError = if (submitted && name.isBlank()) "Ingresa tu nombre" else null
    val emailError = if (submitted && !email.contains("@")) "Ingresa un correo válido" else null
    val documentError =
        if (submitted) validateDocument(documentType, documentNumber) else null
    val passwordError = if (submitted && password.length < 6) "Mínimo 6 caracteres" else null
    val confirmError =
        if (submitted && confirm != password) "Las contraseñas no coinciden" else null

    Scaffold(
        topBar = { AppTopBar(title = AppTexts.CREATE_ACCOUNT, onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            // Aviso sobre los dos roles
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.cardPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.iconMd)
                    )
                    Spacer(Modifier.width(Dimens.spaceMd))
                    Text(
                        text = "Con una sola cuenta puedes publicar solicitudes como " +
                                "Generador y recolectar como Reciclador.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre completo",
                leadingIcon = Icons.Filled.Person,
                errorText = nameError
            )
            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email,
                errorText = emailError
            )

            // ---- Documento de identificación ----
            Text(
                text = AppTexts.FIELD_DOCUMENT_TYPE,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                DocumentType.entries.forEach { type ->
                    MaterialChip(
                        label = type.label,
                        selected = documentType == type,
                        onClick = {
                            documentType = type
                            documentNumber = sanitizeDocument(type, documentNumber)
                        }
                    )
                }
            }
            AppTextField(
                value = documentNumber,
                onValueChange = { documentNumber = sanitizeDocument(documentType, it) },
                label = AppTexts.FIELD_DOCUMENT_NUMBER,
                leadingIcon = Icons.Filled.Badge,
                keyboardType = if (documentType.numericOnly) KeyboardType.Number
                else KeyboardType.Text,
                errorText = documentError
            )

            AppTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Teléfono (opcional)",
                leadingIcon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone
            )
            AppTextField(
                value = neighborhood,
                onValueChange = { neighborhood = it },
                label = "Barrio o localidad (opcional)",
                leadingIcon = Icons.Filled.LocationOn
            )
            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true,
                errorText = passwordError
            )
            AppTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = "Confirmar contraseña",
                leadingIcon = Icons.Filled.Lock,
                isPassword = true,
                errorText = confirmError
            )

            if (submitError != null) {
                Text(
                    text = submitError,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            PrimaryButton(
                text = AppTexts.CREATE_ACCOUNT,
                onClick = {
                    submitted = true
                    val valid = name.isNotBlank() &&
                            email.contains("@") &&
                            validateDocument(documentType, documentNumber) == null &&
                            password.length >= 6 &&
                            confirm == password
                    if (valid) {
                        onRegister(
                            name.trim(),
                            email.trim(),
                            documentType,
                            documentNumber,
                            phone.trim(),
                            neighborhood.trim(),
                            password
                        )
                    }
                }
            )

            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = AppTexts.HAVE_ACCOUNT,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}