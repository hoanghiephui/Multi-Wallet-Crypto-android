package io.horizontalsystems.bankwallet.core

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.applovin.mediation.nativeAds.MaxNativeAdView

abstract class BaseViewModel : ViewModel() {
    private val callbacks = mutableStateListOf<String>()
    private lateinit var nativeAdLoader: MaxTemplateNativeAdViewComposableLoader
    val adState: State<MaxNativeAdView?> = nativeAdLoader.nativeAdView

    /**
     * Log ad callbacks in the LazyColumn.
     * Uses the name of the function that calls this one in the log.
     */
    fun logCallback() {
        val callbackName = Throwable().stackTrace[1].methodName
        callbacks.add(callbackName)
    }

    fun loadAds(
        context: Context,
        adUnitIdentifier: String
    ) {
        // Initialize ad with ad loader.
        nativeAdLoader = MaxTemplateNativeAdViewComposableLoader(adUnitIdentifier, context, this)
        nativeAdLoader.loadAd()
    }

    override fun onCleared() {
        super.onCleared()
        nativeAdLoader.destroy()
    }
}

sealed interface AdsUiState {
    data object Loading : AdsUiState
    data class Success(val adLoader: MaxTemplateNativeAdViewComposableLoader) : AdsUiState
}
