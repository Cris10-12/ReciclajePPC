package com.udistrital.reciclajeppc.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.ui.graphics.vector.ImageVector
import com.udistrital.reciclajeppc.domain.model.MaterialType

fun MaterialType.icon(): ImageVector = when (this) {
    MaterialType.PLASTIC -> Icons.Filled.LocalDrink
    MaterialType.GLASS -> Icons.Filled.WineBar
    MaterialType.PAPER -> Icons.Filled.Description
    MaterialType.CARDBOARD -> Icons.Filled.Inventory2
    MaterialType.METAL -> Icons.Filled.Build
    MaterialType.ELECTRONIC -> Icons.Filled.Devices

}

fun List<MaterialType>.labels(): String = joinToString(", ") { it.label }