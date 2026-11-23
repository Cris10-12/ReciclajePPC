package com.udistrital.reciclajeppc.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.domain.model.User

/** Fila "avatar + nombre + subtítulo" con acciones opcionales a la derecha. */
@Composable
fun UserRow(
    user: User,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = Dimens.spaceSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(name = user.name, avatarUri = user.avatarUri, size = 48.dp)
        Spacer(Modifier.width(Dimens.spaceMd))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(Dimens.spaceSm))
            trailing()
        }
    }
}