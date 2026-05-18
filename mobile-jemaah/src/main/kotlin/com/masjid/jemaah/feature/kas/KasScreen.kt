package com.masjid.jemaah.feature.kas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.jemaah.ui.theme.ExpenseRed
import com.masjid.jemaah.ui.theme.IncomeGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasScreen(
    masjidId: String,
    onNavigateBack: () -> Unit,
    viewModel: KasViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    LaunchedEffect(masjidId) {
        viewModel.handleIntent(KasIntent.LoadKas(masjidId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transparansi Kas") },
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
                is KasState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is KasState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${s.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.handleIntent(KasIntent.LoadKas(masjidId)) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is KasState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ── Summary Card (from BE) ─────────────────────────────
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "Saldo Saat Ini",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        formatter.format(s.summary.balance),
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Total Pemasukan", style = MaterialTheme.typography.labelSmall)
                                            Text(
                                                formatter.format(s.summary.totalIncome),
                                                color = IncomeGreen,
                                                fontWeight = FontWeight.SemiBold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Total Pengeluaran", style = MaterialTheme.typography.labelSmall)
                                            Text(
                                                formatter.format(s.summary.totalExpense),
                                                color = ExpenseRed,
                                                fontWeight = FontWeight.SemiBold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── Transaction List ───────────────────────────────────
                        item {
                            Text("Riwayat Transaksi", style = MaterialTheme.typography.titleMedium)
                        }

                        if (s.transactions.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Belum ada data transaksi.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(s.transactions) { tx ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(tx.title, style = MaterialTheme.typography.titleMedium)
                                            tx.description?.let {
                                                Text(
                                                    it,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
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

                            // ── Load More ──────────────────────────────────────
                            if (s.hasMore) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        OutlinedButton(
                                            onClick = { viewModel.handleIntent(KasIntent.LoadMore(masjidId)) },
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Text("Muat Lebih Banyak")
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
