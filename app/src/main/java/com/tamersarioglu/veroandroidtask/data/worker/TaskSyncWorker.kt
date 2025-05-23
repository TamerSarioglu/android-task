package com.tamersarioglu.veroandroidtask.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tamersarioglu.veroandroidtask.domain.repository.TaskRepository
import com.tamersarioglu.veroandroidtask.utils.Resource
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "task_sync_worker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(WORK_NAME, "Starting background sync...")

        try {
            // Get the repository through Hilt EntryPoint
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                TaskSyncWorkerEntryPoint::class.java
            )
            val repository = entryPoint.taskRepository()

            when (val result = repository.refreshTasks()) {
                is Resource.Success -> {
                    Log.d(WORK_NAME, "Background sync successful")
                    Result.success()
                }
                is Resource.Error -> {
                    Log.e(WORK_NAME, "Background sync failed: ${result.message}")
                    Result.retry()
                }
                is Resource.Loading -> {
                    Log.d(WORK_NAME, "Background sync in progress (retrying)...")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(WORK_NAME, "Background sync exception: ${e.message}")
            Result.retry()
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TaskSyncWorkerEntryPoint {
    fun taskRepository(): TaskRepository
}