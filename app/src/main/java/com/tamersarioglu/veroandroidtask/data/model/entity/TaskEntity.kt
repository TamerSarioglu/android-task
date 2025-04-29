package com.tamersarioglu.veroandroidtask.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tamersarioglu.veroandroidtask.data.local.converter.MapTypeConverter

@Entity(tableName = "tasks")
@TypeConverters(MapTypeConverter::class)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val task: String,
    val title: String,
    val description: String,
    val colorCode: String,
    val additionalProperties: Map<String, String> = emptyMap()
)