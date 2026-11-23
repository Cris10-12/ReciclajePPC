package com.udistrital.reciclajeppc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.udistrital.reciclajeppc.core.util.getInitials

/**
 * Avatar circular: muestra la foto si hay una y, si no, las iniciales.
 * Las iniciales quedan debajo de la foto, así que también se ven si la imagen no carga.
 */
@Composable
fun UserAvatar(
    name: String,
    avatarUri: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getInitials(name),
            style = textStyle,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (avatarUri != null) {
            AsyncImage(
                model = avatarUri,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}