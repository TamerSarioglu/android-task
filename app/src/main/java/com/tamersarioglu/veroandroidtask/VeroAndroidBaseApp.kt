package com.tamersarioglu.veroandroidtask

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.tamersarioglu.veroandroidtask.data.worker.CustomWorkerFactory
import com.tamersarioglu.veroandroidtask.data.worker.TaskSyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class VeroAndroidBaseApp: Application() {

    @Inject
    lateinit var customWorkerFactory: CustomWorkerFactory

    override fun onCreate() {
        super.onCreate()
        schedulePeriodicWorkRequest()
    }

    private fun schedulePeriodicWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<TaskSyncWorker>(
            60, TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            TaskSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}