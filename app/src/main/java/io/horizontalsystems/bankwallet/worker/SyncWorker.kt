package io.horizontalsystems.bankwallet.worker

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import com.wallet.blockchain.bitcoin.BuildConfig
import com.wallet.blockchain.bitcoin.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.horizontalsystems.bankwallet.analytics.AnalyticsHelper
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.getColorCompat
import io.horizontalsystems.bankwallet.modules.main.MainActivity
import io.horizontalsystems.bankwallet.repository.CoinBaseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.TimeUnit


/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
    private val repository: CoinBaseRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo = appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            val result = repository.getPriceCoin(
                base = "USD",
                filter = "listed",
                resolution = "latest"
            )
            val data = result.data ?: return@withContext Result.retry()
            val dataBtc = data.find { it.base == "BTC" }
            val dataETH = data.find { it.base == "ETH" }
            val dataBCH = data.find { it.base == "BCH" }
            val nameBTC = dataBtc?.base
            val nameETH = dataETH?.base
            val nameBCH = dataBCH?.base
            val priceBTC = (dataBtc?.prices?.latestPrice?.percentChange?.day ?: 0.0) * 100
            val priceETH = (dataETH?.prices?.latestPrice?.percentChange?.day ?: 0.0) * 100
            val priceBCH = (dataBCH?.prices?.latestPrice?.percentChange?.day ?: 0.0) * 100
            val priceChangePercentBTC = App.numberFormatter.format(
                priceBTC,
                0,
                2,
                suffix = "%"
            )
            val priceChangePercentETH = App.numberFormatter.format(
                priceETH,
                0,
                2,
                suffix = "%"
            )
            val priceChangePercentBCH = App.numberFormatter.format(
                priceBCH,
                0,
                2,
                suffix = "%"
            )
            val content = if (priceBTC > 0f) appContext.getString(
                R.string.Notification_PriceUp1,
                nameBTC,
                priceChangePercentBTC,
                nameETH,
                priceChangePercentETH,
                nameBCH,
                priceChangePercentBCH
            )
            else appContext.getString(
                R.string.Notification_PriceDown1,
                nameBTC,
                priceChangePercentBTC,
                nameETH,
                priceChangePercentETH,
                nameBCH,
                priceChangePercentBCH
            )
            showNotify(appContext, content)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotify(
        context: Context,
        message: String
    ) {
        val name = "Blockchain"
        val descriptionText = "Shows notifications whenever work starts"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            lightColor = Color.GRAY
        }

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)


        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_notification)
            .setContentTitle(context.getString(R.string.Notification_Title1))
            .setContentText(message)
            .setColor(context.getColorCompat(R.color.issyk_blue))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setGroup(CHANNEL_ID)
            .setGroupSummary(true)
            .setContentIntent(pendingIntent)
            .setVibrate(LongArray(0))

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = BuildConfig.APPLICATION_ID
        private const val NOTIFICATION_ID = 2022

        /**
         * Expedited one time work to sync data on app startup
         */
        private val morningDelay = calculateInitialDelayForTime(7)
        private val eveningDelay = calculateInitialDelayForTime(19)

        val morningWork = PeriodicWorkRequestBuilder<DelegatingWorker>(
            repeatInterval = 24, // Đặt lại mỗi 24 giờ
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, // Độ linh hoạt
            flexTimeIntervalUnit = TimeUnit.MILLISECONDS
        )
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .setInitialDelay(morningDelay, TimeUnit.MINUTES)
            .build()

        val eveningWork = PeriodicWorkRequestBuilder<DelegatingWorker>(
            repeatInterval = 24, // Đặt lại mỗi 24 giờ
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, // Độ linh hoạt
            flexTimeIntervalUnit = TimeUnit.MILLISECONDS
        )
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .setInitialDelay(eveningDelay, TimeUnit.MINUTES)
            .build()

        private fun calculateInitialDelayForTime(hour: Int): Long {
            return if (DateTime.now().hourOfDay < hour) {
                Duration(
                    DateTime.now(),
                    DateTime.now().withTimeAtStartOfDay().plusHours(hour)
                ).standardMinutes
            } else {
                Duration(
                    DateTime.now(),
                    DateTime.now().withTimeAtStartOfDay().plusDays(1).plusHours(hour)
                ).standardMinutes
            }
        }
    }
}
