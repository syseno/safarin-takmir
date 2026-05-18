package com.masjid.takmir.feature.transaction

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
fun TransactionFormScreen(
    financeId: String?,
    onNavigateBack: () -> Unit,
    viewModel: TransactionFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("DEBIT") } // "DEBIT" or "CREDIT"
    var description by remember { mutableStateOf("") }

    LaunchedEffect(financeId) {
        viewModel.initForm(financeId)
    }

    // Populate form when editing
    LaunchedEffect(state) {
        if (state is TransactionFormState.Editing && title.isEmpty() && amount.isEmpty()) {
            val tx = (state as TransactionFormState.Editing).transaction
            title = tx.title
            amount = tx.amount.toString()
            type = tx.type
            description = tx.description ?: ""
        } else if (state is TransactionFormState.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (financeId == null) "Tambah Transaksi" else "Edit Transaksi") },
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
                is TransactionFormState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TransactionFormState.Error -> {
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
                            label = { Text("Judul Transaksi") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Jumlah (Rp)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Jenis Transaksi:")
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = type == "DEBIT",
                                onClick = { type = "DEBIT" }
                            )
                            Text("Pemasukan")
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = type == "CREDIT",
                                onClick = { type = "CREDIT" }
                            )
                            Text("Pengeluaran")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Deskripsi (Opsional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                val amountLong = amount.toLongOrNull() ?: 0L
                                viewModel.saveTransaction(financeId, title, amountLong, type, description)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state !is TransactionFormState.Saving
                        ) {
                            if (state is TransactionFormState.Saving) {
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
