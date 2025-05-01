package com.tamersarioglu.veroandroidtask.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamersarioglu.veroandroidtask.domain.model.Task

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskColorIndicator(color = task.color)
            TaskTexts(task = task)
        }
    }
}

@Composable
fun TaskColorIndicator(color: Color) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .padding(end = 12.dp)
            .background(color, shape = CircleShape)
    )
}

@Composable
fun TaskTexts(task: Task) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = task.task,
            style = MaterialTheme.typography.titleMedium
        )
        if (!task.title.isNullOrBlank()) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (!task.description.isNullOrBlank()) {
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}