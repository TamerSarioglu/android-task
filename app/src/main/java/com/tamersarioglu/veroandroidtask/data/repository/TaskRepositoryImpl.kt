package com.tamersarioglu.veroandroidtask.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.tamersarioglu.veroandroidtask.data.api.ApiService
import com.tamersarioglu.veroandroidtask.data.local.TaskDao
import com.tamersarioglu.veroandroidtask.data.model.dto.AuthRequest
import com.tamersarioglu.veroandroidtask.domain.model.Task
import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import com.tamersarioglu.veroandroidtask.utils.Constants
import com.tamersarioglu.veroandroidtask.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import androidx.core.content.edit
import com.tamersarioglu.veroandroidtask.domain.mapper.toEntity
import com.tamersarioglu.veroandroidtask.domain.mapper.toTask
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow

class TaskRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: TaskDao,
    private val prefs: SharedPreferences
) : TaskRepository {

    override suspend fun login(
        username: String,
        password: String
    ): Resource<String> {
        return try {
            val authRequest = AuthRequest(username, password)
            val response = api.login(authRequest = authRequest)

            if (response.isSuccessful) {
                val authToken = response.body()?.oauth?.access_token
                if (authToken != null) {

                    Log.d("TaskRepository", "Auth successful: ${response.body()}")

                    prefs.edit {
                        putString(Constants.AUTH_TOKEN_KEY, authToken)
                    }

                    Resource.Success(authToken)
                } else {
                    Resource.Error("Authentication failed: Token is null")
                }
            } else {
                Resource.Error("Authentication failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Authentication failed: ${e.localizedMessage}")
        }
    }

    override suspend fun refreshTasks(): Resource<Unit> {
        val token = prefs.getString(Constants.AUTH_TOKEN_KEY, null)
            ?: return Resource.Error("Not authenticated")

        return try {
            val response = api.getTasks()

            if (response.isSuccessful) {
                val tasks = response.body()
                if (tasks != null) {
                    val entities = tasks.map { it.toEntity() }
                    dao.clearAllTasks()
                    dao.insertTasks(entities)
                    Resource.Success(Unit)
                } else {
                    Resource.Error("Failed to fetch tasks: Response body is null")
                }
            } else {
                Resource.Error("Failed to fetch tasks: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch tasks: ${e.localizedMessage}")
        }
    }

    override fun getTasks(): Flow<Resource<List<Task>>> = flow {
        try {
            emit(Resource.Loading())
            dao.getAllTasks().collect { entities ->
                if (entities.isEmpty()) {
                    // If no tasks in database, try to refresh from API
                    val token = prefs.getString(Constants.AUTH_TOKEN_KEY, null)
                        ?: throw Exception("Not authenticated")
                    
                    val response = api.getTasks()
                    if (response.isSuccessful) {
                        val tasks = response.body()
                        if (tasks != null) {
                            val newEntities = tasks.map { it.toEntity() }
                            dao.clearAllTasks()
                            dao.insertTasks(newEntities)
                            emit(Resource.Success(newEntities.map { it.toTask() }))
                        } else {
                            emit(Resource.Error("No tasks available"))
                        }
                    } else {
                        emit(Resource.Error("Failed to fetch tasks: ${response.code()} ${response.message()}"))
                    }
                } else {
                    emit(Resource.Success(entities.map { it.toTask() }))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error loading tasks: ${e.message}"))
        }
    }

    override fun searchTasks(query: String): Flow<Resource<List<Task>>> {
        return dao.searchTasks(query).map { entities ->
            Resource.Success(entities.map { it.toTask() })
        }
    }
}