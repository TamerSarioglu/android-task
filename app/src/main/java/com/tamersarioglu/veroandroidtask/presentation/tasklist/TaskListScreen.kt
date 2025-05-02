package com.tamersarioglu.veroandroidtask.presentation.tasklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tamersarioglu.veroandroidtask.domain.model.Task
import com.tamersarioglu.veroandroidtask.presentation.components.DefaultErrorContent
import com.tamersarioglu.veroandroidtask.presentation.components.ResourceStateView
import com.tamersarioglu.veroandroidtask.presentation.components.TaskItem
import com.tamersarioglu.veroandroidtask.presentation.qrscanner.QrScannerScreen
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val tasksState by viewModel.tasksState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching = remember { mutableStateOf(false) }
    val showQrScanner = remember { mutableStateOf(false) }
    val showPermissionRationale = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                showQrScanner.value = true
            } else {
                showPermissionRationale.value = true
            }
        }
    )

    fun checkAndRequestCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                showQrScanner.value = true
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Scaffold(
        topBar = {
            TaskListTopBar(
                isSearching = isSearching.value,
                onSearchClick = { isSearching.value = !isSearching.value },
                onQrClick = { checkAndRequestCameraPermission() },
                onLogout = onLogout
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedVisibility(
                visible = isSearching.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                TaskSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.searchTasks(it) }
                )
            }
            when {
                showQrScanner.value -> {
                    QrScannerScreen(
                        onQrScanned = { qrValue ->
                            viewModel.searchTasks(qrValue)
                            isSearching.value = true
                            showQrScanner.value = false
                        },
                        onCancel = { showQrScanner.value = false }
                    )
                }
                else -> {
                    ResourceStateView(
                        modifier = Modifier.fillMaxSize(),
                        resource = tasksState,
                        errorContent = { message, _ ->
                            DefaultErrorContent(message = message, onRetry = { viewModel.refreshTasks() })
                        },
                        successContent = { tasks ->
                            val isRefreshing = false
                            val pullToRefreshState = rememberPullToRefreshState()

                            PullToRefreshBox(
                                state = pullToRefreshState,
                                isRefreshing = isRefreshing,
                                onRefresh = { viewModel.refreshTasks() }
                            ) {
                                if (tasks.isEmpty()) {
                                    TaskListEmptyState(searchQuery)
                                } else {
                                    TaskListContent(tasks)
                                }
                            }
                        }
                    )
                }
            }
        }

        if (showPermissionRationale.value) {
            AlertDialog(
                onDismissRequest = { showPermissionRationale.value = false },
                title = { Text("Camera Permission Required") },
                text = { Text("This app needs camera access to scan QR codes. Please grant the permission.") },
                confirmButton = {
                    Button(onClick = {
                        showPermissionRationale.value = false
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

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

@Composable
fun TaskSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search tasks...") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun TaskListEmptyState(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (query.isNotEmpty()) "No tasks found for \"$query\"" else "No tasks available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TaskListContent(tasks: List<Task>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(task = task)
        }
    }
}