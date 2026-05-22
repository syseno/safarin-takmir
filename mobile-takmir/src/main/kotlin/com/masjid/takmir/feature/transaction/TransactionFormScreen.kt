package com.masjid.takmir.feature.transaction

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    financeId: String?,
    onNavigateBack: (Boolean) -> Unit,
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
            onNavigateBack(true)
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
                title = { Text(if (financeId == null) "Tambah Transaksi" else "Edit Transaksi", fontWeight = FontWeight.Bold) },
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
                is TransactionFormState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IslamicGreen)
                }
                is TransactionFormState.Error -> {
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
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Judul Transaksi") },
                            leadingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = IslamicGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                        )

                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Jumlah (Rp)") },
                            leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = IslamicGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Jenis:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = type == "DEBIT",
                                        onClick = { type = "DEBIT" },
                                        colors = RadioButtonDefaults.colors(selectedColor = IslamicGreen)
                                    )
                                    Text("Masuk", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = type == "CREDIT",
                                        onClick = { type = "CREDIT" },
                                        colors = RadioButtonDefaults.colors(selectedColor = IslamicGreen)
                                    )
                                    Text("Keluar", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Keterangan (Opsional)") },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = IslamicGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                        )

                        Spacer(Modifier.weight(1f))

                        Button(
                            onClick = {
                                val amountLong = amount.toLongOrNull() ?: 0L
                                viewModel.saveTransaction(financeId, title, amountLong, type, description)
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen),
                            enabled = state !is TransactionFormState.Saving
                        ) {
                            if (state is TransactionFormState.Saving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                            } else {
                                Text("Simpan Transaksi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
