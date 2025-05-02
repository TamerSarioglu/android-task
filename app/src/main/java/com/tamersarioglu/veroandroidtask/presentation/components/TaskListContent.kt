package com.tamersarioglu.veroandroidtask.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamersarioglu.veroandroidtask.domain.model.Task

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