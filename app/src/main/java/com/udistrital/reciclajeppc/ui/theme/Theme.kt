package com.udistrital.reciclajeppc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = GreenDark,
    onPrimary = WarmWhite,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenDark,
    secondary = GreenMid,
    onSecondary = WarmWhite,
    secondaryContainer = GreenLight,
    onSecondaryContainer = GreenDark,
    background = WarmWhite,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    surfaceVariant = GreenLight,
    onSurfaceVariant = TextMuted,
    outline = GreenMid,
    error = ErrorRed
)

@Composable
fun ReciclajeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}