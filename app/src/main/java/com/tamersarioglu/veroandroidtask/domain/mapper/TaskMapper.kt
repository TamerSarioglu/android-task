package com.tamersarioglu.veroandroidtask.domain.mapper

import androidx.compose.ui.graphics.Color
import com.tamersarioglu.veroandroidtask.data.model.dto.TaskDto
import com.tamersarioglu.veroandroidtask.data.model.entity.TaskEntity
import com.tamersarioglu.veroandroidtask.domain.model.Task
import java.util.UUID
import androidx.core.graphics.toColorInt

fun TaskDto.toEntity(): TaskEntity {
    // Store any potential additional properties
    val additionalProps = mutableMapOf<String, String>()

    return TaskEntity(
        id = UUID.randomUUID().toString(),
        task = task,
        title = title,
        description = description,
        colorCode = colorCode,
        sort = sort,
        wageType = wageType,
        businessUnitKey = businessUnitKey,
        businessUnit = businessUnit,
        parentTaskID = parentTaskID,
        preplanningBoardQuickSelect = preplanningBoardQuickSelect,
        workingTime = workingTime,
        isAvailableInTimeTrackingKioskMode = isAvailableInTimeTrackingKioskMode,
        isAbstract = isAbstract,
        additionalProperties = additionalProps
    )
}

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        task = task,
        title = title,
        description = description,
        colorCode = colorCode,
        color = parseColor(colorCode),
        sort = sort,
        wageType = wageType,
        businessUnitKey = businessUnitKey,
        businessUnit = businessUnit,
        parentTaskID = parentTaskID,
        preplanningBoardQuickSelect = preplanningBoardQuickSelect,
        workingTime = workingTime,
        isAvailableInTimeTrackingKioskMode = isAvailableInTimeTrackingKioskMode,
        isAbstract = isAbstract,
        additionalProperties = additionalProperties
    )
}

fun parseColor(colorCode: String?): Color {
    return try {
        if (colorCode.isNullOrBlank()) return Color.Gray

        val code = colorCode.replace("#", "")
        val color = "#$code".toColorInt()
        Color(color)
    } catch (e: Exception) {
        Color.Gray
    }
}