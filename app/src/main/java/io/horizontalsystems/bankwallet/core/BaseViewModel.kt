package io.horizontalsystems.bankwallet.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applovin.mediation.nativeAds.MaxNativeAdView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val callbacks = mutableStateListOf<String>()
    private val nativeAdLoader: MaxTemplateNativeAdViewComposableLoader by lazy {
        MaxTemplateNativeAdViewComposableLoader(this)
    }
    val adState: State<AdViewState> get() = nativeAdLoader.nativeAdView
    private val _uiState = MutableStateFlow(false)
    val uiState = _uiState.asSharedFlow()

    init {
        App.appLoVinSdk.initializeSdk {
            viewModelScope.launch {
                _uiState.emit(true)
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
            uiState.collect {
                if (it) {
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
