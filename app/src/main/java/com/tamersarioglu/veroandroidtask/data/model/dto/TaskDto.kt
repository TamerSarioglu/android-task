package com.tamersarioglu.veroandroidtask.data.model.dto

data class TaskDto(
    val task: String,
    val title: String,
    val description: String,
    val colorCode: String,
    // We'll add other fields as we discover them from the API response
    val id: String? = null,
    val properties: Map<String, Any>? = null  // For storing other properties that might be in the response
)