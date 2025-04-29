package com.tamersarioglu.veroandroidtask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tamersarioglu.veroandroidtask.data.local.converter.MapTypeConverter
import com.tamersarioglu.veroandroidtask.data.model.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 2, exportSchema = false)
@TypeConverters(MapTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}