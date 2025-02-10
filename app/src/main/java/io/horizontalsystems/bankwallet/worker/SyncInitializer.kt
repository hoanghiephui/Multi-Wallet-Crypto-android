package io.horizontalsystems.bankwallet.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.horizontalsystems.bankwallet.worker.SyncWorker.Companion.eveningWork
import io.horizontalsystems.bankwallet.worker.SyncWorker.Companion.morningWork
import java.util.concurrent.TimeUnit

object Sync {
    // This method is initializes sync, the process that keeps the app's data current.
    // It is called from the app module's Application.onCreate() and should be only done once.
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniquePeriodicWork(
                MORNING_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                morningWork,
            )
        }
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniquePeriodicWork(
                EVENING_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                eveningWork,
            )
        }

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "news-notifications",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            createPeriodicWork(),
        )
    }
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val MORNING_SYNC_WORK_NAME = "MorningSyncWork"
internal const val EVENING_SYNC_WORK_NAME = "EveningSyncWork"

private fun createPeriodicWork(): PeriodicWorkRequest =
    PeriodicWorkRequestBuilder<DelegatingWorker>(15, TimeUnit.MINUTES)
        .setConstraints(SyncConstraints)
        .setInputData(NewsNotificationWorker::class.delegatedData())
        .setInitialDelay(1, TimeUnit.MINUTES)
        .build()
