package com.masjid.takmir.feature.inventory

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

sealed class InventoryFormState {
    object Idle : InventoryFormState()
    object Submitting : InventoryFormState()
    object Success : InventoryFormState()
    data class Error(val message: String) : InventoryFormState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: InventoryFormViewModel = hiltViewModel()
) {
    val state by viewModel.formState.collectAsState()

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var condition by remember { mutableStateOf("GOOD") }
    var conditionExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is InventoryFormState.Success) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Barang") },
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Barang") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(expanded = conditionExpanded, onExpandedChange = { conditionExpanded = it }) {
                OutlinedTextField(
                    value = condition,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kondisi") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(conditionExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = conditionExpanded, onDismissRequest = { conditionExpanded = false }) {
                    listOf("GOOD", "DAMAGED", "LOST").forEach { c ->
                        DropdownMenuItem(text = { Text(c) }, onClick = { condition = c; conditionExpanded = false })
                    }
                }
            }

            if (state is InventoryFormState.Error) {
                Text((state as InventoryFormState.Error).message, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.submit(name, quantity.toIntOrNull() ?: 0, condition)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is InventoryFormState.Submitting
            ) {
                if (state is InventoryFormState.Submitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Simpan Barang")
                }
            }
        }
    }
}
