package com.udistrital.reciclajeppc.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.udistrital.reciclajeppc.core.constants.AppTexts

private const val MAX_BADGE = 99

/** Ícono de mensajes con globo numérico de no leídos (cabecera de las pestañas). */
@Composable
fun MessagesAction(
    unreadCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    IconButton(onClick = onClick, modifier = modifier) {
        BadgedBox(
            badge = {
                if (unreadCount > 0) {
                    Badge {
                        Text(
                            if (unreadCount > MAX_BADGE) AppTexts.BADGE_OVERFLOW
                            else unreadCount.toString()
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.ChatBubble,
                contentDescription = AppTexts.MESSAGES_TITLE,
                tint = tint
            )
        }
    }
}