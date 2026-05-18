package com.masjid.takmir.feature.event

import androidx.compose.foundation.layout.*
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
fun EventFormScreen(
    eventId: String?,
    onNavigateBack: () -> Unit,
    viewModel: EventFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    LaunchedEffect(eventId) {
        viewModel.initForm(eventId)
    }

    LaunchedEffect(state) {
        if (state is EventFormState.Editing && title.isEmpty() && date.isEmpty()) {
            val ev = (state as EventFormState.Editing).event
            title = ev.title
            description = ev.description
            date = ev.date
            startTime = ev.startTime
            endTime = ev.endTime
            location = ev.location ?: ""
        } else if (state is EventFormState.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (eventId == null) "Tambah Event" else "Edit Event") },
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
                is EventFormState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is EventFormState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Kembali")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Judul Event") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Deskripsi") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Simple text inputs for now, could be replaced with Date/Time pickers
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = date,
                                onValueChange = { date = it },
                                label = { Text("Tanggal (YYYY-MM-DD)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                label = { Text("Waktu Mulai (HH:mm)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = endTime,
                                onValueChange = { endTime = it },
                                label = { Text("Waktu Selesai (HH:mm)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Lokasi (Opsional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                viewModel.saveEvent(eventId, title, description, date, startTime, endTime, location)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state !is EventFormState.Saving
                        ) {
                            if (state is EventFormState.Saving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text("Simpan")
                            }
                        }
                    }
                }
            }
        }
    }
}
