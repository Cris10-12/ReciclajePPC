package com.udistrital.reciclajeppc.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.udistrital.reciclajeppc.core.constants.Dimens

@Composable
fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val shape = MaterialTheme.shapes.large
    Card(
        modifier = modifier.then(
            if (onClick != null) Modifier.clip(shape).clickable(onClick = onClick) else Modifier
        ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.spaceMd, horizontal = Dimens.spaceSm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.iconMd)
            )
            Spacer(Modifier.height(Dimens.spaceXs))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
            Text(
                text = if (onClick != null) "$label ›" else label,
                style = MaterialTheme.typography.bodySmall,
                color = if (onClick != null) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (onClick != null) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}