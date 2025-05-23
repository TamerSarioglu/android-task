package com.tamersarioglu.veroandroidtask.presentation.tasklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamersarioglu.veroandroidtask.domain.model.Task
import com.tamersarioglu.veroandroidtask.domain.usecase.GetTasksUseCase
import com.tamersarioglu.veroandroidtask.domain.usecase.LogoutUseCase
import com.tamersarioglu.veroandroidtask.domain.usecase.RefreshTasksUseCase
import com.tamersarioglu.veroandroidtask.domain.usecase.SearchTasksUseCase
import com.tamersarioglu.veroandroidtask.utils.Constants.ERROR_FAILED_TO_LOAD
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_ERROR_LOADING
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_ERROR_REFRESHING
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_LOADING
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_REFRESHING
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_TAG
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_TASKS_LOADED
import com.tamersarioglu.veroandroidtask.utils.Constants.LOG_TASKS_REFRESHED
import com.tamersarioglu.veroandroidtask.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val refreshTasksUseCase: RefreshTasksUseCase,
    private val searchTasksUseCase: SearchTasksUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _tasksState = MutableStateFlow<Resource<List<Task>>>(Resource.Loading())
    val tasksState: StateFlow<Resource<List<Task>>> = _tasksState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                getTasksUseCase().collect { resource ->
                    _tasksState.value = resource
                    when (resource) {
                        is Resource.Success -> {
                            Log.d(LOG_TAG, String.format(LOG_TASKS_LOADED, resource.data?.size ?: 0))
                        }
                        is Resource.Error -> {
                            Log.e(LOG_TAG, String.format(LOG_ERROR_LOADING, resource.message))
                        }
                        is Resource.Loading -> {
                            Log.d(LOG_TAG, LOG_LOADING)
                        }
                    }
                }
            } catch (e: Exception) {
                _tasksState.value = Resource.Error(String.format(ERROR_FAILED_TO_LOAD, e.message))
                Log.e(LOG_TAG, String.format(ERROR_FAILED_TO_LOAD, e.message))
            }
        }
    }

    fun refreshTasks() {
        viewModelScope.launch {
            _tasksState.value = Resource.Loading()
            when (val result = refreshTasksUseCase()) {
                is Resource.Success -> {
                    Log.d(LOG_TAG, LOG_TASKS_REFRESHED)
                    loadTasks()
                }
                is Resource.Error -> {
                    Log.e(LOG_TAG, String.format(LOG_ERROR_REFRESHING, result.message))
                }
                is Resource.Loading -> {
                    Log.d(LOG_TAG, LOG_REFRESHING)
                }
            }
        }
    }

    fun searchTasks(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            searchTasksUseCase(query).collect { resource ->
                _tasksState.value = resource
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _tasksState.value = Resource.Loading()
            _searchQuery.value = ""
        }
    }
}