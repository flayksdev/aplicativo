package dev.flayksdev.candidaturas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.flayksdev.candidaturas.data.JobApplication
import dev.flayksdev.candidaturas.data.Status
import dev.flayksdev.candidaturas.data.filtered
import dev.flayksdev.candidaturas.data.stats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsScreen(viewModel: ApplicationsViewModel) {
    val items by viewModel.items.collectAsState()
    val stats = remember(items) { items.stats() }
    val visible = remember(items, viewModel.statusFilter, viewModel.query) {
        items.filtered(viewModel.statusFilter, viewModel.query)
    }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Candidaturas") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nova") },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = viewModel.statusFilter == null,
                        onClick = { viewModel.setStatusFilter(null) },
                        label = { Text("Todas (${stats.total})") },
                    )
                }
                items(Status.entries.toList()) { status ->
                    FilterChip(
                        selected = viewModel.statusFilter == status,
                        onClick = { viewModel.setStatusFilter(status) },
                        label = { Text("${status.label} (${stats.byStatus[status] ?: 0})") },
                    )
                }
            }

            OutlinedTextField(
                value = viewModel.query,
                onValueChange = viewModel::setQuery,
                label = { Text("Buscar empresa ou vaga") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            if (visible.isEmpty()) {
                Text(
                    text = if (items.isEmpty()) {
                        "Nenhuma candidatura ainda. Toque em \"Nova\" para registrar a primeira."
                    } else {
                        "Nada encontrado com esse filtro."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(24.dp),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visible, key = { it.id }) { app ->
                        ApplicationCard(
                            app = app,
                            onStatus = { viewModel.updateStatus(app.id, it) },
                            onDelete = { viewModel.delete(app.id) },
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        NewApplicationDialog(
            onDismiss = { showDialog = false },
            onSave = { company, role, link ->
                viewModel.add(company, role, link)
                showDialog = false
            },
        )
    }
}

@Composable
private fun ApplicationCard(
    app: JobApplication,
    onStatus: (Status) -> Unit,
    onDelete: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    var menuOpen by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        app.role,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(app.company, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir candidatura")
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { menuOpen = true }) { Text("Status: ${app.status.label}") }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    Status.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.label) },
                            onClick = {
                                onStatus(status)
                                menuOpen = false
                            },
                        )
                    }
                }
                if (app.link.isNotBlank()) {
                    TextButton(onClick = { runCatching { uriHandler.openUri(app.link) } }) {
                        Text("Abrir vaga")
                    }
                }
            }
        }
    }
}

@Composable
private fun NewApplicationDialog(
    onDismiss: () -> Unit,
    onSave: (company: String, role: String, link: String) -> Unit,
) {
    var company by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova candidatura") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(company, { company = it }, label = { Text("Empresa") }, singleLine = true)
                OutlinedTextField(role, { role = it }, label = { Text("Vaga") }, singleLine = true)
                OutlinedTextField(link, { link = it }, label = { Text("Link (opcional)") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(company, role, link) },
                enabled = company.isNotBlank() && role.isNotBlank(),
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
