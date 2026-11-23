package com.udistrital.reciclajeppc.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.cardPadding
import com.udistrital.reciclajeppc.domain.model.User

/**
 * Tarjeta destacada que lleva al perfil de una persona.
 * Si [onToggleFavorite] no es nulo, muestra el corazón de favoritos.
 */
@Composable
fun PersonLinkCard(
    roleLabel: String,
    user: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.cardPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(name = user.name, avatarUri = user.avatarUri, size = 52.dp)
            Spacer(Modifier.width(Dimens.spaceMd))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roleLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppTexts.VIEW_PROFILE,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (onToggleFavorite != null) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) AppTexts.REMOVE_FAVORITE
                        else AppTexts.ADD_FAVORITE,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}