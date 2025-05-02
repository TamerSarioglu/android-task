package com.tamersarioglu.veroandroidtask.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListTopBar(
    isSearching: Boolean,
    onSearchClick: () -> Unit,
    onQrClick: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = { Text("Tasks", style = MaterialTheme.typography.headlineMedium) },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = if (isSearching) "Close Search" else "Search")
            }
            IconButton(onClick = onQrClick) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
            }
        }
    )
}