package com.tamersarioglu.veroandroidtask.domain.usecase

import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import com.tamersarioglu.veroandroidtask.utils.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(username: String, password: String): Resource<String> {
        if (username.isBlank() || password.isBlank()) {
            return Resource.Error("Username and password cannot be empty")
        }
        return repository.login(username, password)
    }
}