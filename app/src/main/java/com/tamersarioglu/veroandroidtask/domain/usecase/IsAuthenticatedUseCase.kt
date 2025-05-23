package com.tamersarioglu.veroandroidtask.domain.usecase

import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import javax.inject.Inject

class IsAuthenticatedUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Boolean {
        return repository.isAuthenticated()
    }
} 