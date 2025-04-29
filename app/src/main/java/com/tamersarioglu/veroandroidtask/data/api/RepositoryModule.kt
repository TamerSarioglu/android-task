package com.tamersarioglu.veroandroidtask.data.api

import com.tamersarioglu.veroandroidtask.data.repository.TaskRepositoryImpl
import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Or another appropriate component
abstract class RepositoryModule {

    @Binds
    @Singleton // Scope should match the implementation scope and where it's needed
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}