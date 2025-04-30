package com.tamersarioglu.veroandroidtask.presentation.tasklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamersarioglu.veroandroidtask.domain.model.Task
import com.tamersarioglu.veroandroidtask.domain.usecase.GetTasksUseCase
import com.tamersarioglu.veroandroidtask.domain.usecase.RefreshTasksUseCase
import com.tamersarioglu.veroandroidtask.domain.usecase.SearchTasksUseCase
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
    private val searchTasksUseCase: SearchTasksUseCase
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
                            Log.d("TaskListViewModel", "Tasks loaded successfully: ${resource.data?.size} tasks")
                        }
                        is Resource.Error -> {
                            Log.e("TaskListViewModel", "Error loading tasks: ${resource.message}")
                        }
                        is Resource.Loading -> {
                            Log.d("TaskListViewModel", "Loading tasks...")
                        }
                    }
                }
            } catch (e: Exception) {
                _tasksState.value = Resource.Error("Failed to load tasks: ${e.message}")
                Log.e("TaskListViewModel", "Error in loadTasks: ${e.message}")
            }
        }
    }

    fun refreshTasks() {
        viewModelScope.launch {
            _tasksState.value = Resource.Loading()
            when (val result = refreshTasksUseCase()) {
                is Resource.Success -> {
                    Log.d("TaskListViewModel", "Tasks refreshed successfully")
                }
                is Resource.Error -> {
                    Log.e("TaskListViewModel", "Error refreshing tasks: ${result.message}")
                }
                is Resource.Loading -> {
                    Log.d("TaskListViewModel", "Refreshing tasks...")
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
}