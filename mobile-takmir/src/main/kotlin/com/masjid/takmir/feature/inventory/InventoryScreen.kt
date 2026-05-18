package com.masjid.takmir.feature.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.core.domain.InventoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventaris") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Default.Add, "Tambah Barang")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is InventoryState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is InventoryState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.handleIntent(InventoryIntent.Refresh) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is InventoryState.Success -> {
                    if (s.items.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Inventory,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("Belum ada barang inventaris.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Summary row
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val good = s.items.count { it.condition == "GOOD" }
                                    val damaged = s.items.count { it.condition == "DAMAGED" }
                                    val lost = s.items.count { it.condition == "LOST" }
                                    ConditionChip("Baik: $good", Color(0xFF4CAF50))
                                    ConditionChip("Rusak: $damaged", Color(0xFFFF9800))
                                    ConditionChip("Hilang: $lost", Color(0xFFF44336))
                                }
                            }
                            items(s.items) { item ->
                                InventoryItemCard(item, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConditionChip(label: String, color: Color) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InventoryItemCard(item: InventoryItem, viewModel: InventoryViewModel) {
    var showConditionDialog by remember { mutableStateOf(false) }
    var quantityText by remember(item.id) { mutableStateOf(item.quantity.toString()) }

    val conditionColor = when (item.condition) {
        "GOOD" -> Color(0xFF4CAF50)
        "DAMAGED" -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                AssistChip(
                    onClick = { showConditionDialog = true },
                    label = { Text(item.condition, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(labelColor = conditionColor)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Jumlah:", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    modifier = Modifier.width(80.dp),
                    singleLine = true
                )
                TextButton(onClick = {
                    quantityText.toIntOrNull()?.let { qty ->
                        viewModel.handleIntent(InventoryIntent.UpdateQuantity(item.id, qty))
                    }
                }) { Text("Simpan") }
            }
        }
    }

    if (showConditionDialog) {
        AlertDialog(
            onDismissRequest = { showConditionDialog = false },
            title = { Text("Ubah Kondisi") },
            text = {
                Column {
                    listOf("GOOD", "DAMAGED", "LOST").forEach { c ->
                        TextButton(
                            onClick = {
                                viewModel.handleIntent(InventoryIntent.UpdateCondition(item.id, c))
                                showConditionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(c) }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
