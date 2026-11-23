package com.udistrital.reciclajeppc.core.util

import java.util.UUID

/** Genera un id corto con prefijo, por ejemplo newId("cn") = "cn-3f9a1b2c". */
fun newId(prefix: String): String = prefix + "-" + UUID.randomUUID().toString().take(8)