package com.masjid.takmir.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.core.domain.UpdateProfileRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addressDetail by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is ProfileState.Success && !isEditing) {
            val profile = (state as ProfileState.Success).profile
            name = profile.name
            phone = profile.phone ?: ""
            addressDetail = profile.addressDetail ?: ""
            description = profile.description ?: ""
        } else if (state is ProfileState.LoggedOut) {
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Masjid") },
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
                is ProfileState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.handleIntent(ProfileIntent.LoadProfile) }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is ProfileState.Success, is ProfileState.LoggedOut -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nama Masjid") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Nomor Telepon") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = addressDetail,
                            onValueChange = { addressDetail = it },
                            label = { Text("Alamat Lengkap") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            enabled = isEditing
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Deskripsi / Info Singkat") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            enabled = isEditing
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        if (isEditing) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                OutlinedButton(
                                    onClick = { isEditing = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Batal")
                                }
                                Button(
                                    onClick = {
                                        viewModel.handleIntent(
                                            ProfileIntent.UpdateProfile(
                                                UpdateProfileRequest(name, phone, description, addressDetail)
                                            )
                                        )
                                        isEditing = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Simpan")
                                }
                            }
                        } else {
                            Button(
                                onClick = { isEditing = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Edit Profil")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = { viewModel.handleIntent(ProfileIntent.Logout) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Logout")
                            }
                        }
                    }
                }
            }
        }
    }
}
