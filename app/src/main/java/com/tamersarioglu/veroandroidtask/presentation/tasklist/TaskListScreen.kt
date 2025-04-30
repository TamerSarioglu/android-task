package com.tamersarioglu.veroandroidtask.presentation.tasklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tamersarioglu.veroandroidtask.domain.model.Task
import com.tamersarioglu.veroandroidtask.utils.Resource
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.TextField
import com.tamersarioglu.veroandroidtask.presentation.qrscanner.QrScannerScreen
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

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
                    if (isSearching.value) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchTasks(it) },
                            placeholder = { Text("Search tasks...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Tasks")
                    }
                },
                actions = {
                    if (isSearching.value) {
                        IconButton(onClick = {
                            isSearching.value = false
                            viewModel.searchTasks("")  // Clear search directly
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
        if (showQrScanner.value) {
            QrScannerScreen(
                onQrScanned = { qrValue ->
                    viewModel.searchTasks(qrValue)  // Use ViewModel directly
                    isSearching.value = true
                    showQrScanner.value = false
                },
                onCancel = { showQrScanner.value = false }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (tasksState) {
                    is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is Resource.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = (tasksState as Resource.Error<List<Task>>).message ?: "Error loading tasks",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.refreshTasks() }
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    is Resource.Success -> {
                        val tasks = (tasksState as Resource.Success<List<Task>>).data ?: emptyList()
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
                                    // Display appropriate message based on whether user is searching
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
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "${task.title} - ${task.task}",
            color = task.color
        )
    }
}