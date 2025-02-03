package io.horizontalsystems.bankwallet.core

import androidx.lifecycle.ViewModel
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import io.horizontalsystems.subscriptions.core.NoAds
import io.horizontalsystems.subscriptions.core.UserSubscriptionManager
import timber.log.Timber

abstract class BaseViewModel : ViewModel(), MaxAdViewAdListener, MaxAdRevenueListener {
    companion object {
        val SHOW_ADS = false// get() = !UserSubscriptionManager.isActionAllowed(NoAds)
    }

    override fun onAdLoaded(ad: MaxAd) {
        Timber.d("Applovin: ${ad.adUnitId}")
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        Timber.e("Applovin: ${error.message}")
    }

    override fun onAdHidden(ad: MaxAd) {

    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {

    }

    override fun onAdDisplayed(ad: MaxAd) {

    }

    override fun onAdClicked(ad: MaxAd) {

    }

    override fun onAdExpanded(ad: MaxAd) {

    }

    override fun onAdCollapsed(ad: MaxAd) {

    }

    override fun onAdRevenuePaid(ad: MaxAd) {

    }
}
