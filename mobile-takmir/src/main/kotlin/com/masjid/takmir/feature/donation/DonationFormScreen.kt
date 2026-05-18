package com.masjid.takmir.feature.donation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: DonationFormViewModel = hiltViewModel()
) {
    val state by viewModel.formState.collectAsState()

    var type by remember { mutableStateOf("SADAQAH") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }

    val donationTypes = listOf("SADAQAH", "INFAQ", "ZAKAT")

    LaunchedEffect(state) {
        if (state is DonationFormState.Success) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catat Donasi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Jenis Donasi") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    donationTypes.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = { type = t; typeExpanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Jumlah (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Keterangan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            if (state is DonationFormState.Error) {
                Text(
                    (state as DonationFormState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.submit(
                        type = type,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        description = description
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is DonationFormState.Submitting
            ) {
                if (state is DonationFormState.Submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan Donasi")
                }
            }
        }
    }
}
