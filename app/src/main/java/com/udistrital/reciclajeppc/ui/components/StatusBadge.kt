package com.udistrital.reciclajeppc.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.ui.theme.ErrorRed
import com.udistrital.reciclajeppc.ui.theme.GreenDark
import com.udistrital.reciclajeppc.ui.theme.GreenLight
import com.udistrital.reciclajeppc.ui.theme.WarningAmber

@Composable
fun StatusBadge(status: RequestStatus, modifier: Modifier = Modifier) {
    val (background, content) = when (status) {
        RequestStatus.AVAILABLE -> GreenLight to GreenDark
        RequestStatus.ACCEPTED -> WarningAmber.copy(alpha = 0.15f) to WarningAmber
        RequestStatus.COMPLETED -> GreenDark to Color.White
        RequestStatus.CANCELLED -> ErrorRed.copy(alpha = 0.12f) to ErrorRed
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = content
        )
    }
}