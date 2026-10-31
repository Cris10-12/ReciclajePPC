package com.udistrital.reciclajeppc.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.Dimens

/** Fila "ícono + etiqueta + valor", usada en detalle de solicitud y perfil. */
@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.iconMd)
        )
        Spacer(Modifier.width(Dimens.spaceSm))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}