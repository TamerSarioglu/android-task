package com.tamersarioglu.veroandroidtask.domain.mapper

import androidx.compose.ui.graphics.Color
import com.tamersarioglu.veroandroidtask.data.model.dto.TaskDto
import com.tamersarioglu.veroandroidtask.data.model.entity.TaskEntity
import com.tamersarioglu.veroandroidtask.domain.model.Task
import java.util.UUID
import androidx.core.graphics.toColorInt

fun TaskDto.toEntity(): TaskEntity {
    val additionalProps = mutableMapOf<String, String>()
    properties?.forEach { (key, value) ->
        additionalProps[key] = value.toString()
    }

    return TaskEntity(
        id = id ?: UUID.randomUUID().toString(),
        task = task,
        title = title,
        description = description,
        colorCode = colorCode,
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
        additionalProperties = additionalProperties
    )
}

fun parseColor(colorCode: String): Color {
    return try {
        val code = colorCode.replace("#", "")
        val color = "#$code".toColorInt()
        Color(color)
    } catch (e: Exception) {
        Color.Gray
    }
}