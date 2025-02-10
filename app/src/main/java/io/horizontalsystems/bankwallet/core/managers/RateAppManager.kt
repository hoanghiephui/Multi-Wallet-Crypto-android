package io.horizontalsystems.bankwallet.core.managers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import io.horizontalsystems.bankwallet.core.IAdapterManager
import io.horizontalsystems.bankwallet.core.ILocalStorage
import io.horizontalsystems.bankwallet.core.IRateAppManager
import io.horizontalsystems.bankwallet.core.IWalletManager
import io.horizontalsystems.bankwallet.ui.helpers.LinkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Instant
import androidx.core.net.toUri
import kotlinx.coroutines.flow.asStateFlow


class RateAppManager(
    private val walletManager: IWalletManager,
    private val adapterManager: IAdapterManager,
    private val localStorage: ILocalStorage
) : IRateAppManager {

    private val _showRateFlow = MutableStateFlow(false)
    override val showRateAppFlow = _showRateFlow.asStateFlow()

    private var isOnBalancePage = false

    override fun onBalancePageActive() {
        isOnBalancePage = true
        showIfAllowed()
    }

    override fun onBalancePageInactive() {
        isOnBalancePage = false
    }

    override fun onAppLaunch() {
        val launchCount = localStorage.appLaunchCount
        if (launchCount < MIN_LAUNCH_COUNT) {
            localStorage.appLaunchCount = launchCount + 1
            return
        }

        val lastRequestTime = localStorage.rateAppLastRequestTime
        if (lastRequestTime > 0 && Instant.now().epochSecond - lastRequestTime < REQUEST_TIME_INTERVAL) {
            return
        }

        startCountdownChecker()
    }

    private fun startCountdownChecker() {
        Handler(Looper.getMainLooper()).postDelayed({ onCountdownPass() }, COUNTDOWN_TIME_INTERVAL)
    }

    private fun onCountdownPass() {
        if (walletManager.activeWallets.size >= MIN_COINS_COUNT) {
            showIfAllowed()
        }
    }

    private fun showIfAllowed() {
        if (isOnBalancePage) {
            localStorage.rateAppLastRequestTime = Instant.now().epochSecond
            _showRateFlow.value = true
        }
    }

    companion object {
        private const val MIN_LAUNCH_COUNT = 5
        private const val MIN_COINS_COUNT = 2
        private const val COUNTDOWN_TIME_INTERVAL = 10_000L // 10 seconds
        private const val REQUEST_TIME_INTERVAL = 10 * 24 * 60 * 60L // 40 Days
        fun openPlayMarket(context: Context) {
            try {
                context.startActivity(getPlayMarketAppIntent())
            } catch (e: ActivityNotFoundException) {
                val appPlayStoreLink =
                    "http://play.google.com/store/apps/details?id=com.blockchain.btc.coinhub"
                LinkHelper.openLinkInAppBrowser(context, appPlayStoreLink)
            }
        }

        private fun getPlayMarketAppIntent(): Intent {
            return Intent(
                Intent.ACTION_VIEW,
                "market://details?id=com.blockchain.btc.coinhub".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            }
        }
    }
}

