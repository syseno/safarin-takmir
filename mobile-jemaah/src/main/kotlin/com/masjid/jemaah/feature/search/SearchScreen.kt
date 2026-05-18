package com.masjid.jemaah.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cari Masjid") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.handleIntent(SearchIntent.SubmitSearch(it))
                },
                label = { Text("Nama atau Lokasi Masjid") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            // Results
            Box(modifier = Modifier.fillMaxSize()) {
                when (val s = state) {
                    is SearchState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is SearchState.Error -> {
                        Text(
                            text = "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                    is SearchState.Success -> {
                        if (s.masjids.isEmpty()) {
                            Text(
                                text = "Tidak ada masjid ditemukan.",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(s.masjids) { masjid ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            onNavigateToDetail(masjid.id)
                                        }
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(masjid.name, style = MaterialTheme.typography.titleMedium)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                masjid.addressDetail ?: "Lokasi tidak tersedia",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
