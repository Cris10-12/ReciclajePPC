package com.udistrital.reciclajeppc.core.util

import java.text.Normalizer

private val DIACRITICS = "\\p{Mn}+".toRegex()

/** Minúsculas y sin tildes, para comparar textos al buscar. */
fun String.normalizeForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(DIACRITICS, "")
        .lowercase()