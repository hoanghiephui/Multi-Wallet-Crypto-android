package io.horizontalsystems.bankwallet.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    private val callbacks = mutableStateListOf<String>()
    private val nativeAdLoader: MaxTemplateNativeAdViewComposableLoader by lazy {
        MaxTemplateNativeAdViewComposableLoader(this)
    }
    val adState: State<AdViewState> get() = nativeAdLoader.nativeAdView
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
            App.appLoVinSdk.initialize(App.appLovinSdkInitialization) {
                nativeAdLoader.loadAd(context, adUnitIdentifier)
                Log.d("Applovin", "loadAds")
            }
        }

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
