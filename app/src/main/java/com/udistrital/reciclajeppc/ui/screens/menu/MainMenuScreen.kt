package com.udistrital.reciclajeppc.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.domain.model.UserRole
import com.udistrital.reciclajeppc.ui.components.BrandHeader
import com.udistrital.reciclajeppc.ui.components.LogoBadge

@Composable
fun MainMenuScreen(
    userName: String,
    onOpenNodes: () -> Unit = {},
    onSelectRole: (UserRole) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        BrandHeader(verticalPadding = 32.dp) {
            LogoBadge(size = 64.dp)
            Spacer(Modifier.height(Dimens.spaceMd))
            Text(
                text = if (userName.isBlank()) "¡Hola!" else "¡Hola, $userName!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.height(Dimens.spaceXs))
            Text(
                text = "¿Cómo quieres participar hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
            )
        }

        Column(
            modifier = Modifier.screenPadding(),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
        ) {
            Spacer(Modifier.height(Dimens.spaceSm))

            RoleCard(
                title = UserRole.GENERATOR.label,
                description = "Publica solicitudes de recolección para tus residuos reciclables.",
                icon = Icons.Filled.Storefront,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                iconBackground = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.primary,
                onClick = { onSelectRole(UserRole.GENERATOR) }
            )

            RoleCard(
                title = UserRole.RECYCLER.label,
                description = "Encuentra solicitudes cercanas y organiza tus recolecciones.",
                icon = Icons.Filled.LocalShipping,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                iconBackground = MaterialTheme.colorScheme.primary,
                iconTint = MaterialTheme.colorScheme.onPrimary,
                onClick = { onSelectRole(UserRole.RECYCLER) }
            )

            Text(
                text = "Puedes cambiar de rol en cualquier momento desde tu perfil.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(onClick = onOpenNodes, modifier = Modifier.fillMaxWidth()) {
                Text("Red distribuida (demo)", color = MaterialTheme.colorScheme.primary)
            }
            TextButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun RoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spaceLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(Dimens.iconMd)
                )
            }
            Spacer(Modifier.width(Dimens.spaceMd))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.85f)
                )
            }
            Spacer(Modifier.width(Dimens.spaceSm))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null
            )
        }
    }
}