package com.masjid.jemaah.feature.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    masjidId: String,
    onNavigateBack: () -> Unit,
    viewModel: EventViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(masjidId) {
        viewModel.handleIntent(EventIntent.LoadEvents(masjidId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Kajian & Event") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                        Button(onClick = { viewModel.handleIntent(EventIntent.LoadEvents(masjidId)) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is EventState.Success -> {
                    if (s.events.isEmpty()) {
                        Text(
                            text = "Belum ada event terjadwal.",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(s.events) { event ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(event.title, style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        val formattedDate = remember(event.date) {
                                            com.masjid.jemaah.utils.DateUtils.formatToHumanReadable(event.date)
                                        }
                                        Text(
                                            "$formattedDate • ${event.startTime} - ${event.endTime}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (!event.location.isNullOrBlank()) {
                                            Text(
                                                event.location ?: "",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            event.description,
                                            style = MaterialTheme.typography.bodyMedium
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
