package com.masjid.takmir.feature.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.takmir.ui.theme.IslamicGreen
import com.masjid.takmir.ui.theme.IslamicGreenDark

sealed class InventoryFormState {
    object Idle : InventoryFormState()
    object Submitting : InventoryFormState()
    object Success : InventoryFormState()
    data class Error(val message: String) : InventoryFormState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryFormScreen(
    onNavigateBack: (Boolean) -> Unit,
    viewModel: InventoryFormViewModel = hiltViewModel()
) {
    val state by viewModel.formState.collectAsState()

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var condition by remember { mutableStateOf("GOOD") }
    var conditionExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is InventoryFormState.Success) onNavigateBack(true)
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
                title = { Text("Tambah Barang Inventaris", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(false) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Barang") },
                leadingIcon = { Icon(Icons.Default.Inventory, contentDescription = null, tint = IslamicGreen) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Jumlah Unit") },
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, tint = IslamicGreen) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
            )

            ExposedDropdownMenuBox(expanded = conditionExpanded, onExpandedChange = { conditionExpanded = it }) {
                OutlinedTextField(
                    value = condition,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kondisi Barang") },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IslamicGreen) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(conditionExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                )
                ExposedDropdownMenu(expanded = conditionExpanded, onDismissRequest = { conditionExpanded = false }) {
                    val conditions = listOf("GOOD" to "Baik", "DAMAGED" to "Rusak", "LOST" to "Hilang")
                    conditions.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = { condition = code; conditionExpanded = false }
                        )
                    }
                }
            }

            if (state is InventoryFormState.Error) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        (state as InventoryFormState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { onNavigateBack(false) }, colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)) {
                        Text("Kembali")
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.submit(name, quantity.toIntOrNull() ?: 0, condition)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                enabled = state !is InventoryFormState.Submitting
            ) {
                if (state is InventoryFormState.Submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Simpan Barang", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
