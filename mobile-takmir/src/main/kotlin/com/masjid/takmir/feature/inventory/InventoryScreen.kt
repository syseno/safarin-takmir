package com.masjid.takmir.feature.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.masjid.core.domain.InventoryItem
import com.masjid.takmir.ui.theme.IslamicGreen
import com.masjid.takmir.ui.theme.IslamicGreenDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            viewModel.handleIntent(InventoryIntent.Refresh)
        }
    }

    LaunchedEffect(state) {
        if (state is InventoryState.Success || state is InventoryState.Error) {
            pullToRefreshState.endRefresh()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.handleIntent(InventoryIntent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
                title = { Text("Inventaris Masjid", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToForm,
                containerColor = IslamicGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Barang")
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)) {
            when (val s = state) {
                is InventoryState.Loading -> {
                    if (!pullToRefreshState.isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IslamicGreen)
                    }
                }
                is InventoryState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.handleIntent(InventoryIntent.Refresh) },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is InventoryState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // ── Header Decoration ──────────────────────────────
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(IslamicGreenDark, Color.Transparent)
                                        )
                                    )
                            )
                        }

                        // Summary Statistics
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .offset(y = (-40).dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val good = s.inventoryList.count { it.condition == "GOOD" }
                                val damaged = s.inventoryList.count { it.condition == "DAMAGED" }
                                val lost = s.inventoryList.count { it.condition == "LOST" }
                                ConditionBadge("Baik: $good", Color(0xFF22C55E), modifier = Modifier.weight(1f))
                                ConditionBadge("Rusak: $damaged", Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                                ConditionBadge("Hilang: $lost", Color(0xFFEF4444), modifier = Modifier.weight(1f))
                            }
                        }

                        if (s.inventoryList.isEmpty() && !pullToRefreshState.isRefreshing) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(48.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Inventory, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Belum ada barang inventaris.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(s.inventoryList) { item ->
                                Box(modifier = Modifier
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                                    .offset(y = (-40).dp)) {
                                    InventoryItemCard(item, viewModel)
                                }
                            }
                        }
                    }
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ConditionBadge(label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            label,
            modifier = Modifier.padding(vertical = 8.dp),
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InventoryItemCard(item: InventoryItem, viewModel: InventoryViewModel) {
    var showConditionDialog by remember { mutableStateOf(false) }
    var quantityText by remember(item.id) { mutableStateOf(item.quantity.toString()) }

    val conditionColor = when (item.condition) {
        "GOOD" -> Color(0xFF22C55E)
        "DAMAGED" -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(
                    onClick = { showConditionDialog = true },
                    color = conditionColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        item.condition,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = conditionColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Jumlah Unit", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    modifier = Modifier.width(70.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen)
                )
                IconButton(
                    onClick = {
                        quantityText.toIntOrNull()?.let { qty ->
                            viewModel.handleIntent(InventoryIntent.UpdateQuantity(item.id, qty))
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = IslamicGreen)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Simpan")
                }
            }
        }
    }

    if (showConditionDialog) {
        AlertDialog(
            onDismissRequest = { showConditionDialog = false },
            title = { Text("Ubah Kondisi Barang", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val conditions = listOf("GOOD" to "Baik", "DAMAGED" to "Rusak", "LOST" to "Hilang")
                    conditions.forEach { (code, label) ->
                        OutlinedButton(
                            onClick = {
                                viewModel.handleIntent(InventoryIntent.UpdateCondition(item.id, code))
                                showConditionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showConditionDialog = false }) { Text("Batal") }
            }
        )
    }
}
