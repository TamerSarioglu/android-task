package com.tamersarioglu.veroandroidtask.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tamersarioglu.veroandroidtask.domain.usecase.RefreshTasksUseCase
import com.tamersarioglu.veroandroidtask.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskSyncWorker(
    context: Context,
    params: WorkerParameters,
    private val refreshTasksUseCase: RefreshTasksUseCase
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "task_sync_worker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(WORK_NAME, "Starting background sync...")

        try {
            when (val result = refreshTasksUseCase()) {
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