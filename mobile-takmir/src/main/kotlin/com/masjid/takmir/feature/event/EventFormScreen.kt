package com.masjid.takmir.feature.event

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.masjid.takmir.ui.theme.IslamicGreen
import com.masjid.takmir.ui.theme.IslamicGreenDark
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
    eventId: String?,
    onNavigateBack: (Boolean) -> Unit,
    viewModel: EventFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Picker states
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showRecurrenceEndPicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()
    val recurrenceEndPickerState = rememberDatePickerState()

    // Image state
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var imageFilename by remember { mutableStateOf<String?>(null) }
    var currentImageUrl by remember { mutableStateOf<String?>(null) }
    var localImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // Recurrence state
    var isRecurring by remember { mutableStateOf(false) }
    var recurrenceType by remember { mutableStateOf("DAILY") }
    var recurrenceInterval by remember { mutableStateOf(1) }
    var recurrenceDays by remember { mutableStateOf(setOf<Int>()) }
    var recurrenceEnd by remember { mutableStateOf("") }

    // Dialog state
    var showEditDialog by remember { mutableStateOf(false) }
    var eventGroupId by remember { mutableStateOf<String?>(null) }

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
            currentImageUrl = ev.imageUrl
            eventGroupId = ev.groupId
        } else if (state is EventFormState.Success) {
            onNavigateBack(true)
        }
    }

    // Helper for formatting
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK", color = IslamicGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = IslamicGreen))
        }
    }

    // Recurrence End Picker Dialog
    if (showRecurrenceEndPicker) {
        DatePickerDialog(
            onDismissRequest = { showRecurrenceEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    recurrenceEndPickerState.selectedDateMillis?.let {
                        recurrenceEnd = sdf.format(Date(it))
                    }
                    showRecurrenceEndPicker = false
                }) { Text("OK", color = IslamicGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showRecurrenceEndPicker = false }) { Text("Batal") }
            }
        ) {
            DatePicker(state = recurrenceEndPickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = IslamicGreen))
        }
    }

    // Time Picker Dialogs
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    startTime = "${startTimePickerState.hour.toTwoDigits()}:${startTimePickerState.minute.toTwoDigits()}"
                    showStartTimePicker = false
                }) { Text("OK", color = IslamicGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) { Text("Batal") }
            }
        ) {
            TimePicker(state = startTimePickerState, colors = TimePickerDefaults.colors(selectorColor = IslamicGreen))
        }
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    endTime = "${endTimePickerState.hour.toTwoDigits()}:${endTimePickerState.minute.toTwoDigits()}"
                    showEndTimePicker = false
                }) { Text("OK", color = IslamicGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) { Text("Batal") }
            }
        ) {
            TimePicker(state = endTimePickerState, colors = TimePickerDefaults.colors(selectorColor = IslamicGreen))
        }
    }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            localImageUri = uri
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    imageBytes = stream.readBytes()
                    imageFilename = "poster_${System.currentTimeMillis()}.jpg"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Event Berulang", fontWeight = FontWeight.Bold) },
            text = { Text("Event ini adalah bagian dari seri event berulang. Apakah Anda ingin mengubah hanya event ini atau semua event dalam seri?") },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            showEditDialog = false
                            viewModel.saveEvent(
                                eventId = eventId,
                                title = title,
                                description = description,
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                location = location,
                                imageBytes = imageBytes,
                                imageFilename = imageFilename,
                                currentImageUrl = currentImageUrl,
                                recurrenceType = if (isRecurring) recurrenceType else null,
                                recurrenceInterval = recurrenceInterval,
                                recurrenceDays = if (recurrenceDays.isEmpty()) null else recurrenceDays.joinToString(","),
                                recurrenceEnd = recurrenceEnd.ifBlank { null },
                                updateType = "ALL"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
                    ) {
                        Text("Semua event dalam seri")
                    }
                    OutlinedButton(
                        onClick = {
                            showEditDialog = false
                            viewModel.saveEvent(
                                eventId = eventId,
                                title = title,
                                description = description,
                                date = date,
                                startTime = startTime,
                                endTime = endTime,
                                location = location,
                                imageBytes = imageBytes,
                                imageFilename = imageFilename,
                                currentImageUrl = currentImageUrl,
                                recurrenceType = if (isRecurring) recurrenceType else null,
                                recurrenceInterval = recurrenceInterval,
                                recurrenceDays = if (recurrenceDays.isEmpty()) null else recurrenceDays.joinToString(","),
                                recurrenceEnd = recurrenceEnd.ifBlank { null },
                                updateType = "SINGLE"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hanya event ini")
                    }
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IslamicGreenDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = { Text(if (eventId == null) "Tambah Event" else "Edit Event", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(false) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is EventFormState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IslamicGreen)
                }
                is EventFormState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onNavigateBack(false) }, colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)) {
                            Text("Kembali")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        // Event Poster Section
                        Text("Poster Event", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (localImageUri != null) {
                                AsyncImage(
                                    model = localImageUri,
                                    contentDescription = "Selected Poster",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = {
                                        localImageUri = null
                                        imageBytes = null
                                        imageFilename = null
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear Image", tint = Color.Red)
                                }
                            } else if (!currentImageUrl.isNullOrBlank()) {
                                val fullUrl = if (currentImageUrl!!.startsWith("http")) currentImageUrl else "http://10.0.2.2:3000$currentImageUrl"
                                AsyncImage(
                                    model = fullUrl,
                                    contentDescription = "Current Poster",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = {
                                        currentImageUrl = null
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear Image", tint = Color.Red)
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = IslamicGreen, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Pilih Poster Event", color = IslamicGreen, fontWeight = FontWeight.Bold)
                                    Text("Format gambar JPEG/PNG", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Judul Event") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Deskripsi") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                            OutlinedTextField(
                                value = date,
                                onValueChange = { },
                                label = { Text("Tanggal") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = IslamicGreen) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLeadingIconColor = IslamicGreen
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f).clickable { showStartTimePicker = true }) {
                                OutlinedTextField(
                                    value = startTime,
                                    onValueChange = { },
                                    label = { Text("Mulai") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    shape = RoundedCornerShape(12.dp),
                                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = IslamicGreen) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledLeadingIconColor = IslamicGreen
                                    )
                                )
                            }
                            Box(modifier = Modifier.weight(1f).clickable { showEndTimePicker = true }) {
                                OutlinedTextField(
                                    value = endTime,
                                    onValueChange = { },
                                    label = { Text("Selesai") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = false,
                                    shape = RoundedCornerShape(12.dp),
                                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = IslamicGreen) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledLeadingIconColor = IslamicGreen
                                    )
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Lokasi (Opsional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = IslamicGreen) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                        )

                        // Recurrence Options (Only allowed on Creation)
                        if (eventId == null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isRecurring,
                                            onCheckedChange = { isRecurring = it },
                                            colors = CheckboxDefaults.colors(checkedColor = IslamicGreen)
                                        )
                                        Text("Event Berulang (Rutin)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    }

                                    if (isRecurring) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("Frekuensi", style = MaterialTheme.typography.titleSmall)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val types = listOf("DAILY", "WEEKLY", "MONTHLY")
                                            val labels = listOf("Harian", "Mingguan", "Bulanan")
                                            types.forEachIndexed { idx, type ->
                                                FilterChip(
                                                    selected = recurrenceType == type,
                                                    onClick = { recurrenceType = type },
                                                    label = { Text(labels[idx]) },
                                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IslamicGreen, selectedLabelColor = Color.White)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                        OutlinedTextField(
                                            value = recurrenceInterval.toString(),
                                            onValueChange = { recurrenceInterval = it.toIntOrNull() ?: 1 },
                                            label = { Text("Interval (misal: 2 minggu)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                                        )

                                        if (recurrenceType == "WEEKLY") {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text("Pilih Hari", style = MaterialTheme.typography.titleSmall)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                val days = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
                                                val dayValues = listOf(0, 1, 2, 3, 4, 5, 6)
                                                dayValues.forEachIndexed { idx, valOf ->
                                                    val isSelected = recurrenceDays.contains(valOf)
                                                    FilterChip(
                                                        selected = isSelected,
                                                        onClick = {
                                                            recurrenceDays = if (isSelected) {
                                                                recurrenceDays - valOf
                                                            } else {
                                                                recurrenceDays + valOf
                                                            }
                                                        },
                                                        label = { Text(days[idx]) },
                                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = IslamicGreen, selectedLabelColor = Color.White)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                        Box(modifier = Modifier.fillMaxWidth().clickable { showRecurrenceEndPicker = true }) {
                                            OutlinedTextField(
                                                value = recurrenceEnd,
                                                onValueChange = { },
                                                label = { Text("Tanggal Berakhir") },
                                                modifier = Modifier.fillMaxWidth(),
                                                enabled = false,
                                                shape = RoundedCornerShape(12.dp),
                                                leadingIcon = { Icon(Icons.Default.EventAvailable, contentDescription = null, tint = IslamicGreen) },
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    disabledLeadingIconColor = IslamicGreen
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                if (eventId != null && eventGroupId != null) {
                                    showEditDialog = true
                                } else {
                                    viewModel.saveEvent(
                                        eventId = eventId,
                                        title = title,
                                        description = description,
                                        date = date,
                                        startTime = startTime,
                                        endTime = endTime,
                                        location = location,
                                        imageBytes = imageBytes,
                                        imageFilename = imageFilename,
                                        currentImageUrl = currentImageUrl,
                                        recurrenceType = if (isRecurring) recurrenceType else null,
                                        recurrenceInterval = recurrenceInterval,
                                        recurrenceDays = if (recurrenceDays.isEmpty()) null else recurrenceDays.joinToString(","),
                                        recurrenceEnd = recurrenceEnd.ifBlank { null },
                                        updateType = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                            enabled = state !is EventFormState.Saving
                        ) {
                            if (state is EventFormState.Saving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text("Simpan Event", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Pilih Waktu",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = { content() }
    )
}

fun Int.toTwoDigits(): String = if (this < 10) "0$this" else this.toString()
