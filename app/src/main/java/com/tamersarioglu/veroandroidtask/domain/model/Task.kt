package com.tamersarioglu.veroandroidtask.domain.model

import androidx.compose.ui.graphics.Color

data class Task(
    val id: String,
    val task: String,
    val title: String?,
    val description: String?,
    val colorCode: String?,
    val color: Color,
    val sort: String?,
    val wageType: String?,
    val businessUnitKey: String?,
    val businessUnit: String?,
    val parentTaskID: String?,
    val preplanningBoardQuickSelect: String?,
    val workingTime: String?,
    val isAvailableInTimeTrackingKioskMode: Boolean,
    val isAbstract: Boolean,
    val additionalProperties: Map<String, String> = emptyMap()
)