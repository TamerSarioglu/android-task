package com.tamersarioglu.veroandroidtask.domain.usecase

import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
} 