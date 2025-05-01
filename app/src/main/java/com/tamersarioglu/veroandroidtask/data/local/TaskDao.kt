package com.tamersarioglu.veroandroidtask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tamersarioglu.veroandroidtask.data.model.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE " +
            "task LIKE '%' || :query || '%' OR " +
            "title LIKE '%' || :query || '%' OR " +
            "description LIKE '%' || :query || '%' OR " +
            "colorCode LIKE '%' || :query || '%' OR " +
            "sort LIKE '%' || :query || '%' OR " +
            "wageType LIKE '%' || :query || '%' OR " +
            "businessUnitKey LIKE '%' || :query || '%' OR " +
            "businessUnit LIKE '%' || :query || '%' OR " +
            "parentTaskID LIKE '%' || :query || '%' OR " +
            "preplanningBoardQuickSelect LIKE '%' || :query || '%' OR " +
            "workingTime LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}