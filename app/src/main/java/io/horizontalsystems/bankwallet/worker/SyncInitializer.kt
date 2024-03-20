package io.horizontalsystems.bankwallet.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import io.horizontalsystems.bankwallet.worker.SyncWorker.Companion.eveningWork
import io.horizontalsystems.bankwallet.worker.SyncWorker.Companion.morningWork

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
    }
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val MORNING_SYNC_WORK_NAME = "MorningSyncWork"
internal const val EVENING_SYNC_WORK_NAME = "EveningSyncWork"
