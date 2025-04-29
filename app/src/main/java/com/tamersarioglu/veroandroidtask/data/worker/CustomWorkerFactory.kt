package com.tamersarioglu.veroandroidtask.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.tamersarioglu.veroandroidtask.domain.usecase.RefreshTasksUseCase
import javax.inject.Inject

class CustomWorkerFactory @Inject constructor(
    private val refreshTasksUseCase: RefreshTasksUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            TaskSyncWorker::class.java.name -> {
                TaskSyncWorker(appContext, workerParameters, refreshTasksUseCase)
            }
            else -> null // Let the default factory handle other workers
        }
    }
} 