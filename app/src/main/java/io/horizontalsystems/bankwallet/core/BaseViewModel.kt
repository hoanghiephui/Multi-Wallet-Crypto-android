package io.horizontalsystems.bankwallet.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

abstract class BaseViewModel : ViewModel(), MaxAdViewAdListener, MaxAdRevenueListener {
    private val callbacks = mutableStateListOf<String>()
    private val nativeAdLoader: MaxTemplateNativeAdViewComposableLoader by lazy {
        MaxTemplateNativeAdViewComposableLoader(this)
    }
    val adState: StateFlow<AdViewState> get() = nativeAdLoader.nativeAdView
    companion object{
        const val SHOW_ADS = true
    }

    /**
     * Log ad callbacks in the LazyColumn.
     * Uses the name of the function that calls this one in the log.
     */
    fun logCallback() {
        val callbackName = Throwable().stackTrace[1].methodName
        callbacks.add(callbackName)
        Log.d("Applovin", callbackName)
    }

    fun loadAds(
        context: Context,
        adUnitIdentifier: String
    ) {
        // Initialize ad with ad loader.
        if (SHOW_ADS) {
            nativeAdLoader.loadAd(context, adUnitIdentifier)
        }

    }

    override fun onCleared() {
        super.onCleared()
        nativeAdLoader.destroy()
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

sealed interface AdsUiState {
    data object Loading : AdsUiState
    data class Success(val adLoader: MaxTemplateNativeAdViewComposableLoader) : AdsUiState
}
