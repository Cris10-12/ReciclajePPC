package com.udistrital.reciclajeppc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.core.constants.Dimens

/** Encabezado verde con esquinas inferiores redondeadas. */
@Composable
fun BrandHeader(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 48.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
            .padding(vertical = verticalPadding, horizontal = Dimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

/** Círculo verde claro con el ícono de reciclaje. */
@Composable
fun LogoBadge(size: Dp = 88.dp, modifier: Modifier = Modifier) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Recycling,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}