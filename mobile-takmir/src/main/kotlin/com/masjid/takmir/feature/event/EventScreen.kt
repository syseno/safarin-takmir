package com.masjid.takmir.feature.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.core.domain.MasjidEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForm: (String?) -> Unit,
    viewModel: EventViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var eventToDelete by remember { mutableStateOf<MasjidEvent?>(null) }

    if (eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Hapus Event") },
            text = { Text("Apakah Anda yakin ingin menghapus event '${eventToDelete?.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    eventToDelete?.id?.let { id ->
                        viewModel.handleIntent(EventIntent.DeleteEvent(id))
                    }
                    eventToDelete = null
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Masjid") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is EventState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is EventState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.handleIntent(EventIntent.Refresh) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is EventState.Success -> {
                    if (s.events.isEmpty()) {
                        Text(
                            text = "Belum ada event.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(s.events) { event ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        onNavigateToForm(event.id)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(event.title, style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                "${event.date} • ${event.startTime} - ${event.endTime}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (!event.location.isNullOrBlank()) {
                                                Text(
                                                    event.location ?: "",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        IconButton(onClick = { eventToDelete = event }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
