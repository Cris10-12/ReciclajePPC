package com.udistrital.reciclajeppc.ui.screens.nodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.reciclajeppc.data.remote.NodeApi
import com.udistrital.reciclajeppc.data.remote.NodePost
import kotlinx.coroutines.launch

private data class NodeOption(val name: String, val port: Int)

private val NODES = listOf(
    NodeOption("central", 8081),
    NodeOption("norte", 8082),
    NodeOption("sur", 8083)
)

/** Pantalla de demostracion: la app habla con el backend distribuido real. */
@Composable
fun NodesScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    var host by remember { mutableStateOf("10.0.2.2") } // emulador -> PC. En celular real: IP de la PC
    var selected by remember { mutableStateOf(NODES[0]) }
    var userId by remember { mutableStateOf("u1") }
    var text by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Listo") }
    var posts by remember { mutableStateOf<List<NodePost>>(emptyList()) }
    var detail by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBack) { Text("< Volver") }
        Text(
            "Red distribuida (demo)",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Elige a que nodo le habla la app:", style = MaterialTheme.typography.bodyMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NODES.forEach { n ->
                FilterChip(
                    selected = selected == n,
                    onClick = { selected = n },
                    label = { Text("${n.name} :${n.port}") }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                scope.launch { detail = NodeApi.raw(host, selected.port, "/monitor") }
            }) { Text("Monitor") }

            OutlinedButton(onClick = {
                scope.launch { detail = NodeApi.raw(host, selected.port, "/connections/$userId") }
            }) { Text("Conexiones") }
        }

        OutlinedTextField(
            value = host, onValueChange = { host = it },
            label = { Text("Host (10.0.2.2 = emulador)") },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = userId, onValueChange = { userId = it },
            label = { Text("Usuario (u1..u60)") },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = text, onValueChange = { text = it },
            label = { Text("Mensaje a publicar") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                scope.launch {
                    status = "Publicando en ${selected.name}..."
                    NodeApi.publish(host, selected.port, userId, text)
                        .onSuccess { status = "Publicado en ${selected.name} OK"; text = "" }
                        .onFailure { status = "Error: ${it.message}" }
                }
            }) { Text("Publicar") }

            OutlinedButton(onClick = {
                scope.launch {
                    status = "Leyendo feed de $userId en ${selected.name}..."
                    NodeApi.feed(host, selected.port, userId)
                        .onSuccess { posts = it; status = "Feed de ${selected.name}: ${it.size} posts" }
                        .onFailure { status = "Error: ${it.message}" }
                }
            }) { Text("Ver feed") }

            OutlinedButton(onClick = {
                scope.launch {
                    status = "${selected.name}: ${NodeApi.health(host, selected.port)}"
                }
            }) { Text("Health") }
        }

        Text(status, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        if (detail.isNotBlank()) {
            Text(detail.take(1500), style = MaterialTheme.typography.bodySmall)
        }
        posts.take(15).forEach { p ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(p.text, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "${p.authorId} - origen: ${p.originNode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
