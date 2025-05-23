package com.tamersarioglu.veroandroidtask.domain.repository

import com.tamersarioglu.veroandroidtask.domain.model.Task
import com.tamersarioglu.veroandroidtask.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun login(username: String, password: String): Resource<String>
    suspend fun logout()
    fun isAuthenticated(): Boolean
    suspend fun refreshTasks(): Resource<Unit>
    fun getTasks(): Flow<Resource<List<Task>>>
    fun searchTasks(query: String): Flow<Resource<List<Task>>>
}