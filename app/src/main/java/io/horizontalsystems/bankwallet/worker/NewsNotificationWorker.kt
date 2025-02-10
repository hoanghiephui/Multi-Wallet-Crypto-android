package io.horizontalsystems.bankwallet.worker

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.wallet.blockchain.bitcoin.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.modules.main.MainActivity
import kotlinx.coroutines.rx2.await

@HiltWorker
class NewsNotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {
    private val marketKit by lazy { App.marketKit }

    override suspend fun getForegroundInfo(): ForegroundInfo = context.newsForegroundInfo()

    override suspend fun doWork(): Result {
        try {
            val posts = marketKit.postsSingle().await()
            if (posts.isNotEmpty()) {
                createNotificationChannelIfNeeded(context)
                val notificationManager = NotificationManagerCompat.from(context)
                val groupKey = "com.blockchain.btc.coinhub.NEWS_NOTIFICATIONS"

                // Gửi từng thông báo chi tiết
                posts.take(5).forEach {
                    val intent = Intent(context, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        action = INTENT_NEWS_NOTIFICATION
                        putExtra("news", it.url)
                    }
                    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

                    val notification = NotificationCompat.Builder(context, NEWS_NOTIFICATION_CHANNEL_ID)
                        .setGroup(groupKey)
                        .setContentTitle("CoinDex | News from ${it.source}")
                        .setContentText(it.title)
                        .setSmallIcon(R.drawable.ic_logo_notification)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build()

                    if (checkSelfPermission(context, permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
                        notificationManager.notify(it.title.hashCode(), notification)
                    }
                }

                // Gửi thông báo nhóm (summary notification)
                val summaryNotification = NotificationCompat.Builder(context, NEWS_NOTIFICATION_CHANNEL_ID)
                    .setGroup(groupKey)
                    .setGroupSummary(true) // Đây là thông báo tổng hợp
                    .setContentTitle("CoinDex | Crypto News")
                    .setContentText("You have ${posts.size} new articles.")
                    .setSmallIcon(R.drawable.ic_logo_notification)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(0, summaryNotification) // ID phải cố định để tránh ghi đè
            }
            return Result.success()
        } catch (ex: Exception) {
            return Result.retry()
        }
    }


    private fun createNotificationChannelIfNeeded(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(NEWS_NOTIFICATION_CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                NEWS_NOTIFICATION_CHANNEL_ID,
                "CoinDex | Crypto News",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows notifications whenever work starts"
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                lightColor = Color.GRAY
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val INTENT_NEWS_NOTIFICATION = "com.blockchain.btc.coinhub.NEWS_NOTIFICATION"
    }
}