package com.tamersarioglu.veroandroidtask.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamersarioglu.veroandroidtask.utils.Resource

@Composable
fun <T> ResourceStateView(
    modifier: Modifier = Modifier,
    resource: Resource<T>,
    loadingContent: @Composable () -> Unit = { DefaultLoadingIndicator() },
    errorContent: @Composable (message: String, onRetry: (() -> Unit)?) -> Unit = { message, onRetry ->
        DefaultErrorContent(
            message = message,
            onRetry = onRetry
        )
    },
    successContent: @Composable (data: T) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (resource) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    loadingContent()
                }
            }

            is Resource.Error -> {
                val errorMessage = resource.message ?: "An unexpected error occurred"
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    errorContent(errorMessage, null)
                }
            }

            is Resource.Success -> {
                resource.data?.let { data ->
                    successContent(data)
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        errorContent("No data available", null)
                    }
                }
            }
        }
    }
}

@Composable
fun DefaultLoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun DefaultErrorContent(message: String, onRetry: (() -> Unit)?) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .background(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                shape = MaterialTheme.shapes.medium
            )
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        onRetry?.let {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = it,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
} 