package com.masjid.takmir.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.takmir.ui.theme.ExpenseRed
import com.masjid.takmir.ui.theme.IncomeGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToDonation: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val s = state) {
                        is DashboardState.Success -> Text(s.masjidName.ifBlank { "Dashboard Takmir" })
                        else -> Text("Dashboard Takmir")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val s = state) {
                is DashboardState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DashboardState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.handleIntent(DashboardIntent.Refresh) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is DashboardState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // ── Saldo Card ─────────────────────────────────────────
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "Total Saldo Kas",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        formatter.format(s.totalSaldo),
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Pemasukan", style = MaterialTheme.typography.labelSmall)
                                            Text(
                                                formatter.format(s.totalIncome),
                                                color = IncomeGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Pengeluaran", style = MaterialTheme.typography.labelSmall)
                                            Text(
                                                formatter.format(s.totalExpense),
                                                color = ExpenseRed,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── Quick Actions (5 modules) ──────────────────────────
                        item {
                            Text("Menu", style = MaterialTheme.typography.titleMedium)
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                QuickActionCard("Kas", Icons.Default.AccountBalanceWallet, onNavigateToTransactions, Modifier.weight(1f))
                                QuickActionCard("Event", Icons.Default.Event, onNavigateToEvents, Modifier.weight(1f))
                                QuickActionCard("Donasi", Icons.Default.Favorite, onNavigateToDonation, Modifier.weight(1f))
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                QuickActionCard("Inventaris", Icons.Default.Inventory, onNavigateToInventory, Modifier.weight(1f))
                                QuickActionCard("Profil", Icons.Default.Person, onNavigateToProfile, Modifier.weight(1f))
                                // Placeholder for symmetry
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        // ── Donation summary ───────────────────────────────────
                        if (s.donationSummary.totalAmount > 0) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Total Donasi", style = MaterialTheme.typography.labelLarge)
                                        Text(
                                            formatter.format(s.donationSummary.totalAmount),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }

                        // ── Inventory ──────────────────────────────────────────
                        if (s.inventoryTotal > 0) {
                            item {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Total Barang Inventaris", style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${s.inventoryTotal} item",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // ── Recent Transactions ────────────────────────────────
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Transaksi Terbaru", style = MaterialTheme.typography.titleMedium)
                        }
                        if (s.recentTransactions.isEmpty()) {
                            item {
                                Text(
                                    "Belum ada transaksi.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(s.recentTransactions.take(5)) { tx ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(tx.title, style = MaterialTheme.typography.titleMedium)
                                            Text(
                                                tx.createdAt,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            "${if (tx.type == "DEBIT") "+" else "-"} ${formatter.format(tx.amount)}",
                                            color = if (tx.type == "DEBIT") IncomeGreen else ExpenseRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // ── Upcoming Events ────────────────────────────────────
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Event Mendatang", style = MaterialTheme.typography.titleMedium)
                        }
                        if (s.upcomingEvents.isEmpty()) {
                            item {
                                Text(
                                    "Belum ada event.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(s.upcomingEvents.take(3)) { event ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(event.title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            "${event.date} • ${event.startTime} - ${event.endTime}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        event.location?.let {
                                            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
