package com.udistrital.reciclajeppc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.udistrital.reciclajeppc.core.navigation.AppNavGraph
import com.udistrital.reciclajeppc.ui.theme.ReciclajeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as RecicladoApp).container
        setContent {
            ReciclajeTheme {
                AppNavGraph(container = container)            }
        }
    }
}