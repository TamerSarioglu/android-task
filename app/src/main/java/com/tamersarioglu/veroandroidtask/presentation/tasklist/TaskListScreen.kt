package com.tamersarioglu.veroandroidtask.presentation.tasklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.tamersarioglu.veroandroidtask.presentation.components.DefaultErrorContent
import com.tamersarioglu.veroandroidtask.presentation.components.ResourceStateView
import com.tamersarioglu.veroandroidtask.presentation.components.TaskItem
import com.tamersarioglu.veroandroidtask.presentation.qrscanner.QrScannerScreen

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Tasks")
                },
                actions = {
                    if (isSearching.value) {
                        IconButton(onClick = {
                            isSearching.value = false
                            viewModel.searchTasks("")
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Close Search")
                        }
                    } else {
                        IconButton(onClick = { isSearching.value = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    IconButton(onClick = { showQrScanner.value = true }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR Code")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            AnimatedVisibility(
                visible = isSearching.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchTasks(it) },
                    placeholder = { Text("Search tasks...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
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
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (searchQuery.isNotEmpty()) {
                                            Text("No tasks found for \"$searchQuery\"")
                                        } else {
                                            Text("No tasks available")
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(tasks) { task ->
                                            TaskItem(task = task)
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}