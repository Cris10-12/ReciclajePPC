package com.udistrital.reciclajeppc.core.util

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.udistrital.reciclajeppc.core.constants.Dimens

/** Margen estándar de todas las pantallas */
fun Modifier.screenPadding(): Modifier = this.padding(Dimens.screenPadding)

/** Relleno estándar interno de las tarjetas */
fun Modifier.cardPadding(): Modifier = this.padding(Dimens.cardPadding)