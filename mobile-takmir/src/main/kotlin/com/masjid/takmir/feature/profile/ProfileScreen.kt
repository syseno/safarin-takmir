package com.masjid.takmir.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.masjid.core.domain.UpdateProfileRequest
import com.masjid.takmir.ui.theme.IslamicGreen
import com.masjid.takmir.ui.theme.IslamicGreenDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showMethodDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addressDetail by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(state.profile) {
        state.profile?.let { profile ->
            if (!isEditing) {
                name = profile.name
                phone = profile.phone ?: ""
                addressDetail = profile.addressDetail ?: ""
                description = profile.description ?: ""
            }
        }
    }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogout()
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
                title = { Text("Profil Masjid", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                // ── Header Decoration ──────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(IslamicGreenDark, Color.Transparent)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .offset(y = (-40).dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Icon/Placeholder
                    Surface(
                        modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = IslamicGreen, modifier = Modifier.size(40.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Masjid") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = IslamicGreen) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                    )
                    
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Nomor Telepon") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = IslamicGreen) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                    )
                    
                    OutlinedTextField(
                        value = addressDetail,
                        onValueChange = { addressDetail = it },
                        label = { Text("Alamat Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        enabled = isEditing,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = IslamicGreen) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                    )
                    
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi / Info Singkat") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        enabled = isEditing,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = IslamicGreen) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IslamicGreen, focusedLabelColor = IslamicGreen)
                    )

                    if (!isEditing) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pengaturan Aplikasi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Theme Selection
                        val themeLabel = when(state.themeMode) {
                            1 -> "Terang"
                            2 -> "Gelap"
                            else -> "Ikuti Sistem"
                        }
                        
                        SettingItem(
                            title = "Tema Aplikasi",
                            subtitle = themeLabel,
                            icon = Icons.Default.Palette,
                            onClick = { showThemeDialog = true }
                        )

                        // Method Selection
                        val currentMethod = state.prayerMethods.values.find { it.id == state.selectedMethodId }
                        val methodName = currentMethod?.name ?: "Kementerian Agama Republik Indonesia"
                        
                        SettingItem(
                            title = "Metode Perhitungan",
                            subtitle = methodName,
                            icon = Icons.Default.Calculate,
                            onClick = { showMethodDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isEditing) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(
                                onClick = { isEditing = false },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Batal")
                            }
                            Button(
                                onClick = {
                                    viewModel.handleIntent(
                                        ProfileIntent.UpdateProfile(
                                            UpdateProfileRequest(name = name, phone = phone, description = description, addressDetail = addressDetail)
                                        )
                                    )
                                    isEditing = false
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
                            ) {
                                Text("Simpan")
                            }
                        }
                    } else {
                        Button(
                            onClick = { isEditing = true },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profil Masjid")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            onClick = { showLogoutConfirm = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Keluar dari Akun",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IslamicGreen)
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
                )
            }
        }
    }

    if (showThemeDialog) {
        SelectionDialog(
            title = "Pilih Tema Aplikasi",
            options = listOf(0 to "Ikuti Sistem", 1 to "Terang", 2 to "Gelap"),
            selectedId = state.themeMode,
            onSelect = { mode -> 
                viewModel.handleIntent(ProfileIntent.SelectTheme(mode))
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showMethodDialog && state.prayerMethods.isNotEmpty()) {
        SelectionDialog(
            title = "Pilih Metode Perhitungan",
            options = state.prayerMethods.values.sortedBy { it.name }.map { it.id!! to it.name },
            selectedId = state.selectedMethodId,
            isLoading = state.isMethodLoading,
            onSelect = { id -> viewModel.handleIntent(ProfileIntent.SelectPrayerMethod(id)) },
            onDismiss = { if (!state.isMethodLoading) showMethodDialog = false }
        )
    }

    // Auto-close method dialog after loading finishes
    LaunchedEffect(state.isMethodLoading) {
        if (!state.isMethodLoading && showMethodDialog) {
            showMethodDialog = false
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Konfirmasi Keluar", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin keluar dari akun Anda?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.handleIntent(ProfileIntent.Logout)
                        showLogoutConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = IslamicGreen.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = IslamicGreen, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(subtitle, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun SelectionDialog(
    title: String,
    options: List<Pair<Int, String>>,
    selectedId: Int,
    isLoading: Boolean = false,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .selectableGroup()
                    ) {
                        options.forEach { (id, label) ->
                            val isSelected = id == selectedId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .selectable(
                                        selected = isSelected,
                                        enabled = !isLoading,
                                        onClick = { onSelect(id) },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null,
                                    enabled = !isLoading,
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
