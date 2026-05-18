package com.masjid.takmir.feature.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.core.domain.Finance
import com.masjid.takmir.ui.theme.ExpenseRed
import com.masjid.takmir.ui.theme.IncomeGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForm: (String?) -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    var transactionToDelete by remember { mutableStateOf<Finance?>(null) }

    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Hapus Transaksi") },
            text = { Text("Apakah Anda yakin ingin menghapus transaksi '${transactionToDelete?.title}'?") },
            confirmButton = {
                TextButton(onClick = {
                    transactionToDelete?.id?.let { id ->
                        viewModel.handleIntent(TransactionIntent.DeleteTransaction(id))
                    }
                    transactionToDelete = null
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kas Masjid") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = state) {
                is TransactionState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TransactionState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.handleIntent(TransactionIntent.Refresh) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is TransactionState.Success -> {
                    if (s.transactions.isEmpty()) {
                        Text(
                            text = "Belum ada transaksi.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(s.transactions) { tx ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        onNavigateToForm(tx.id)
                                    }
                                ) {
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
                                        IconButton(onClick = { transactionToDelete = tx }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error
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
}
