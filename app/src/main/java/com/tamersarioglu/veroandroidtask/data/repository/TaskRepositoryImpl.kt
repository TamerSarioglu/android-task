package com.tamersarioglu.veroandroidtask.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.tamersarioglu.veroandroidtask.data.api.ApiService
import com.tamersarioglu.veroandroidtask.data.local.TaskDao
import com.tamersarioglu.veroandroidtask.data.model.dto.AuthRequest
import com.tamersarioglu.veroandroidtask.domain.mapper.toEntity
import com.tamersarioglu.veroandroidtask.domain.mapper.toTask
import com.tamersarioglu.veroandroidtask.domain.model.Task
import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import com.tamersarioglu.veroandroidtask.utils.Constants
import com.tamersarioglu.veroandroidtask.utils.Constants.AUTH_FAILED_API
import com.tamersarioglu.veroandroidtask.utils.Constants.AUTH_FAILED_EXCEPTION
import com.tamersarioglu.veroandroidtask.utils.Constants.AUTH_FAILED_TOKEN_NULL
import com.tamersarioglu.veroandroidtask.utils.Constants.AUTH_SUCCESS_LOG
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_FETCH_FAILED
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_LOADING_TASKS
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_NOT_AUTHENTICATED
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_REFRESH_UNEXPECTED_STATE
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_REFRESH_UNKNOWN
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_TAG_REPO
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_USING_LOCAL_TASKS
import com.tamersarioglu.veroandroidtask.utils.Resource
import com.tamersarioglu.veroandroidtask.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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

                    Log.d(LOG_TAG_REPO, String.format(AUTH_SUCCESS_LOG, response.body().toString()))

                    prefs.edit {
                        putString(Constants.AUTH_TOKEN_KEY, authToken)
                    }

                    Resource.Success(authToken)
                } else {
                    Resource.Error(AUTH_FAILED_TOKEN_NULL)
                }
            } else {
                Resource.Error(String.format(AUTH_FAILED_API, response.code(), response.message()))
            }
        } catch (e: Exception) {
            Resource.Error(String.format(AUTH_FAILED_EXCEPTION, e.localizedMessage))
        }
    }

    override suspend fun logout() {
        prefs.edit {
            remove(Constants.AUTH_TOKEN_KEY)
        }
        dao.clearAllTasks()
        Log.d(LOG_TAG_REPO, "User logged out successfully")
    }

    override fun isAuthenticated(): Boolean {
        return prefs.getString(Constants.AUTH_TOKEN_KEY, null) != null
    }

    override suspend fun refreshTasks(): Resource<Unit> {
        if (prefs.getString(Constants.AUTH_TOKEN_KEY, null) == null) {
            return Resource.Error(ERROR_NOT_AUTHENTICATED)
        }

        val localTasksCount = dao.getTasksCount()
        if (localTasksCount > 0) {
            Log.d(LOG_TAG_REPO, LOG_USING_LOCAL_TASKS)
            return Resource.Success(Unit)
        }
        
        val result = safeApiCall { api.getTasks() }

        return when (result) {
            is Resource.Success -> {
                val tasks = result.data
                val entities = tasks!!.map { it.toEntity() }
                dao.clearAllTasks()
                dao.insertTasks(entities)
                Resource.Success(Unit)
            }
            is Resource.Error -> {
                Resource.Error(result.message ?: ERROR_REFRESH_UNKNOWN)
            }
            is Resource.Loading -> {
                Resource.Error(ERROR_REFRESH_UNEXPECTED_STATE)
            }
        }
    }

    override fun getTasks(): Flow<Resource<List<Task>>> = flow {
        emit(Resource.Loading())
        try {
            val localTasks = dao.getAllTasks()
            localTasks.collect { entities ->
                if (entities.isEmpty()) {
                    if (prefs.getString(Constants.AUTH_TOKEN_KEY, null) == null) {
                        emit(Resource.Error(ERROR_NOT_AUTHENTICATED))
                        return@collect
                    }

                    when (val networkResult = safeApiCall { api.getTasks() }) {
                        is Resource.Success -> {
                            val tasksDto = networkResult.data
                            val newEntities = tasksDto!!.map { it.toEntity() }
                            dao.clearAllTasks()
                            dao.insertTasks(newEntities)
                            emit(Resource.Success(newEntities.map { it.toTask() }))
                        }
                        is Resource.Error -> {
                            emit(Resource.Error(networkResult.message ?: ERROR_FETCH_FAILED))
                        }
                        is Resource.Loading -> {
                            emit(Resource.Loading())
                        }
                    }
                } else {
                    emit(Resource.Success(entities.map { it.toTask() }))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(String.format(ERROR_LOADING_TASKS, e.message)))
        }
    }

    override fun searchTasks(query: String): Flow<Resource<List<Task>>> {
        return dao.searchTasks(query).map { entities ->
            Resource.Success(entities.map { it.toTask() })
        }
    }
}