package com.udistrital.reciclajeppc.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.udistrital.reciclajeppc.core.navigation.Routes
import com.udistrital.reciclajeppc.domain.model.UserRole
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox


data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val generatorItems = listOf(
    BottomNavItem(Routes.GENERATOR_HOME, "Inicio", Icons.Filled.Home),
    BottomNavItem(Routes.MY_REQUESTS, "Solicitudes", Icons.Filled.Inventory2),
    BottomNavItem(Routes.MESSAGES, "Mensajes", Icons.Filled.ChatBubble),
    BottomNavItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
)

private val recyclerItems = listOf(
    BottomNavItem(Routes.RECYCLER_HOME, "Inicio", Icons.Filled.Home),
    BottomNavItem(Routes.EXPLORE, "Explorar", Icons.Filled.Search),
    BottomNavItem(Routes.AGENDA, "Agenda", Icons.Filled.CalendarMonth),
    BottomNavItem(Routes.MESSAGES, "Mensajes", Icons.Filled.ChatBubble),
    BottomNavItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
)

fun bottomNavItemsFor(role: UserRole): List<BottomNavItem> =
    if (role == UserRole.GENERATOR) generatorItems else recyclerItems

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    badgeCounts: Map<String, Int> = emptyMap()
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemClick(item.route) },
                icon = {
                    val count = badgeCounts[item.route] ?: 0
                    if (count > 0) {
                        BadgedBox(badge = { Badge { Text(count.toString()) } }) {
                            Icon(item.icon, contentDescription = item.label)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}