package com.tamersarioglu.veroandroidtask.domain.usecase

import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import com.tamersarioglu.veroandroidtask.utils.Resource
import javax.inject.Inject

class RefreshTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.refreshTasks()
    }
}