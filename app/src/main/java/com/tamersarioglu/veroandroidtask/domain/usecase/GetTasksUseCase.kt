package com.tamersarioglu.veroandroidtask.domain.usecase

import com.tamersarioglu.veroandroidtask.domain.model.Task
import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import com.tamersarioglu.veroandroidtask.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<Resource<List<Task>>> {
        return repository.getTasks()
    }
}
