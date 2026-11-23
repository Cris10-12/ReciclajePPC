package com.udistrital.reciclajeppc.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatClock(millis: Long): String =
    SimpleDateFormat("h:mm a", Locale("es", "CO")).format(Date(millis))