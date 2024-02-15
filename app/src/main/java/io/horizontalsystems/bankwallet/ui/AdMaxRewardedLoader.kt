package io.horizontalsystems.bankwallet.ui

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import io.horizontalsystems.bankwallet.dialogLoading
import timber.log.Timber

class AdMaxRewardedLoader(private val callback: AdRewardedCallback) {

    private lateinit var rewardedAd: MaxRewardedAd
    private var dialog: AlertDialog? = null
    fun createRewardedAd(
        activity: Activity,
        adUnitId: String
    ) {
        dialog = dialogLoading(activity)
        rewardedAd = MaxRewardedAd.getInstance(adUnitId, activity)
        rewardedAd.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(ad: MaxAd) {
                dialog?.hide()
                dialog = null
                callback.onLoaded(rewardedAd)
            }

            override fun onAdDisplayed(ad: MaxAd) {
                dialog?.hide()
                dialog = null
            }

            override fun onAdHidden(ad: MaxAd) {
                dialog?.hide()
                dialog = null
            }

            override fun onAdClicked(ad: MaxAd) {

            }

            override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
                dialog?.hide()
                dialog = null
                callback.onAdRewardLoadFail()
                Timber.e("ADS Load Fail: ${error.message}")
            }

            override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
                dialog?.hide()
                dialog = null
                callback.onShowFail()
            }

            override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {
                dialog?.hide()
                dialog = null
                callback.onUserRewarded(reward.amount)
                rewardedAd.destroy()
            }

            @Deprecated("Deprecated in Java")
            override fun onRewardedVideoStarted(p0: MaxAd) {

            }

            @Deprecated("Deprecated in Java")
            override fun onRewardedVideoCompleted(p0: MaxAd) {

            }

        })
        rewardedAd.loadAd()
        dialog?.show()
    }
}

interface AdRewardedCallback {
    fun onLoaded(rewardedAd: MaxRewardedAd)
    fun onAdRewardLoadFail()
    fun onUserRewarded(amount: Int)
    fun onShowFail()
}
