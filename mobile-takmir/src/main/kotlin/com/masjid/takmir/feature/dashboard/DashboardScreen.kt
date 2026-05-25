package com.masjid.takmir.feature.dashboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.masjid.core.domain.AdzanTime
import com.masjid.core.domain.DailyAdzanSchedule
import com.masjid.takmir.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToDonation: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val pullToRefreshState = rememberPullToRefreshState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.refreshWithLocation()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        viewModel.refreshWithLocation()
    }

    // Observe refresh flag from backstack
    val refreshNeeded by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refresh", false)
        ?.collectAsState() ?: remember { mutableStateOf(false) }

    LaunchedEffect(refreshNeeded) {
        if (refreshNeeded) {
            viewModel.handleIntent(DashboardIntent.Refresh)
            navController.currentBackStackEntry?.savedStateHandle?.set("refresh", false)
        }
    }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            viewModel.handleIntent(DashboardIntent.Refresh)
        }
    }

    LaunchedEffect(state) {
        if (state is DashboardState.Success || state is DashboardState.Error) {
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IslamicGreenDark,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Column {
                        Text(
                            text = "Marhaban, Takmir",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        when (val s = state) {
                            is DashboardState.Success -> {
                                val locationText = listOfNotNull(s.currentCity, s.currentProvince).joinToString(", ")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = s.masjidName.ifBlank { "Masjid" },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    if (locationText.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(modifier = Modifier.size(3.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.5f)) {}
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(10.dp), tint = IslamicGold)
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = locationText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                            else -> {}
                        }
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when (val s = state) {
                is DashboardState.Loading -> {
                    if (!pullToRefreshState.isRefreshing) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IslamicGreen)
                    }
                }
                is DashboardState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.handleIntent(DashboardIntent.Refresh) },
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicGreen)
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is DashboardState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // ── Header Decoration ──────────────────────────────
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(IslamicGreenDark, Color.Transparent)
                                        )
                                    )
                            )
                        }

                        // ── Prayer Schedule Section ───────────────────────
                        item {
                            if (s.adzanSchedules.isNotEmpty()) {
                                Box(modifier = Modifier.offset(y = (-200).dp)) {
                                    AdzanScheduleSection(s.adzanSchedules, countdown)
                                }
                            } else {
                                Box(modifier = Modifier.offset(y = (-200).dp)) {
                                    LocationAccessCard(locationPermissionLauncher)
                                }
                            }
                        }

                        // ── Saldo Kas Card ─────────────────────────────────────────
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .offset(y = (-180).dp),
                                shape = RoundedCornerShape(24.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text(
                                        "Total Saldo Kas",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        formatter.format(s.totalSaldo),
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(modifier = Modifier.size(8.dp), shape = CircleShape, color = IncomeGreen) {}
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Pemasukan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text(
                                                formatter.format(s.totalIncome),
                                                color = IncomeGreen,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(modifier = Modifier.size(8.dp), shape = CircleShape, color = ExpenseRed) {}
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Pengeluaran", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text(
                                                formatter.format(s.totalExpense),
                                                color = ExpenseRed,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── Menu Utama Header ──────────────────────────
                        item {
                            Text(
                                "Menu Utama",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else IslamicGreenDark,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .offset(y = (-160).dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        // ── Quick Actions ──────────────────────────
                        item {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .offset(y = (-160).dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    QuickActionCard("Laporan Kas", Icons.Default.AccountBalanceWallet, IslamicGreen, onNavigateToTransactions, Modifier.weight(1f))
                                    QuickActionCard("Event Masjid", Icons.Default.Event, Color(0xFF3B82F6), onNavigateToEvents, Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    QuickActionCard("Donasi", Icons.Default.Favorite, ExpenseRed, onNavigateToDonation, Modifier.weight(1f))
                                    QuickActionCard("Inventaris", Icons.Default.Inventory, IslamicGold, onNavigateToInventory, Modifier.weight(1f))
                                }
                            }
                        }

                        // ── Recent Activity Section Header ─────────────────
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Transaksi Terbaru",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else IslamicGreenDark,
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .offset(y = (-140).dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (s.recentTransactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(48.dp)
                                        .offset(y = (-140).dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Belum ada transaksi terbaru.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(s.recentTransactions.take(5)) { tx ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp, vertical = 6.dp)
                                        .offset(y = (-140).dp)
                                ) {
                                    TransactionItem(tx, formatter)
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
fun LocationAccessCard(launcher: androidx.activity.result.ActivityResultLauncher<Array<String>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Temukan Jadwal Sholat di Sekitarmu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "Aktifkan akses lokasi untuk melihat jadwal shalat dan menyesuaikan waktu adzan di masjid ini.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { 
                    launcher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Beri Akses Lokasi")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdzanScheduleSection(schedules: List<DailyAdzanSchedule>, countdown: String) {
    val pagerState = rememberPagerState(pageCount = { schedules.size })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) { page ->
        AdzanArtisticCard(schedules[page], if (page == 0) countdown else null)
    }
}

@Composable
fun AdzanArtisticCard(schedule: DailyAdzanSchedule, countdown: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(IslamicGreen, IslamicGreenDark)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = schedule.dayLabel,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = schedule.hijriDate ?: schedule.date,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                if (countdown != null && countdown != "--:--:--") {
                    Surface(
                        color = IslamicGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Menuju Adzan",
                                style = MaterialTheme.typography.labelSmall,
                                color = IslamicGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = countdown,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Grid-like layout for prayer times
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunks = schedule.times.chunked(2)
                chunks.forEach { rowTimes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowTimes.forEach { time ->
                            AdzanModernItem(time, modifier = Modifier.weight(1f))
                        }
                        if (rowTimes.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdzanModernItem(time: AdzanTime, modifier: Modifier = Modifier) {
    val backgroundColor = if (time.isNext) IslamicGold else Color.White.copy(alpha = 0.1f)
    val contentColor = if (time.isNext) IslamicGreenDark else Color.White
    
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(if (time.isNext) Modifier.shadow(4.dp, RoundedCornerShape(16.dp)) else Modifier),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = time.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (time.isNext) FontWeight.ExtraBold else FontWeight.Medium,
                color = contentColor
            )
            Text(
                text = time.time,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TransactionItem(tx: com.masjid.core.domain.Finance, formatter: NumberFormat) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = (if (tx.type == "DEBIT") IncomeGreen else ExpenseRed).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (tx.type == "DEBIT") Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (tx.type == "DEBIT") IncomeGreen else ExpenseRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(tx.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        tx.createdAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                "${if (tx.type == "DEBIT") "+" else "-"} ${formatter.format(tx.amount)}",
                color = if (tx.type == "DEBIT") IncomeGreen else ExpenseRed,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
