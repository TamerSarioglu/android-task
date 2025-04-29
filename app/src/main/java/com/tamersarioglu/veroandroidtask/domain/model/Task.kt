package com.tamersarioglu.veroandroidtask.domain.model

import androidx.compose.ui.graphics.Color

data class Task(
    val id: String,
    val task: String,
    val title: String,
    val description: String,
    val colorCode: String,
    val color: Color,
    val additionalProperties: Map<String, String> = emptyMap()
)