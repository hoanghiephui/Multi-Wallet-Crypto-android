package io.horizontalsystems.bankwallet.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val callbacks = mutableStateListOf<String>()
    private val nativeAdLoader: MaxTemplateNativeAdViewComposableLoader by lazy {
        MaxTemplateNativeAdViewComposableLoader(this)
    }
    val adState: State<AdViewState> get() = nativeAdLoader.nativeAdView
    private val _uiState = MutableStateFlow(false)
    private val loadAdState = _uiState.asSharedFlow()
    companion object{
        const val SHOW_ADS = true
    }
    init {
        if (SHOW_ADS) {
            App.appLoVinSdk.initializeSdk {
                viewModelScope.launch {
                    _uiState.emit(true)
                }
            }
        }
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
        viewModelScope.launch {
            loadAdState.collect {
                if (it && SHOW_ADS) {
                    nativeAdLoader.loadAd(context, adUnitIdentifier)
                    Log.d("Applovin", "loadAds")
                }
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
