package io.horizontalsystems.bankwallet.modules.settings.security.passcode

import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.wallet.blockchain.bitcoin.BuildConfig
import io.horizontalsystems.bankwallet.core.ILocalStorage
import io.horizontalsystems.bankwallet.core.ViewModelUiState
import io.horizontalsystems.bankwallet.core.managers.BalanceHiddenManager
import io.horizontalsystems.core.IPinComponent
import io.horizontalsystems.core.ISystemInfoManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow

class SecuritySettingsViewModel(
    private val systemInfoManager: ISystemInfoManager,
    private val pinComponent: IPinComponent,
    private val balanceHiddenManager: BalanceHiddenManager,
    private val localStorage: ILocalStorage
) : ViewModelUiState<SecuritySettingsUiState>() {
    val biometricSettingsVisible = systemInfoManager.biometricAuthSupported

    private var pinEnabled = pinComponent.isPinSet
    private var duressPinEnabled = pinComponent.isDuressPinSet()
    private var balanceAutoHideEnabled = balanceHiddenManager.balanceAutoHidden
    private var analyticLog = localStorage.isAnalytic
    private var detectCrash = localStorage.isDetectCrash

    init {
        viewModelScope.launch {
            pinComponent.pinSetFlowable.asFlow().collect {
                pinEnabled = pinComponent.isPinSet
                duressPinEnabled = pinComponent.isDuressPinSet()
                emitState()
            }
        }
    }

    override fun createState() = SecuritySettingsUiState(
        pinEnabled = pinEnabled,
        biometricsEnabled = pinComponent.isBiometricAuthEnabled,
        duressPinEnabled = duressPinEnabled,
        balanceAutoHideEnabled = balanceAutoHideEnabled,
        autoLockIntervalName = localStorage.autoLockInterval.title,
        analyticLog = analyticLog,
        detectCrash = detectCrash
    )

    fun enableBiometrics() {
        pinComponent.isBiometricAuthEnabled = true
        emitState()
    }

    fun disableBiometrics() {
        pinComponent.isBiometricAuthEnabled = false
        emitState()
    }

    fun disablePin() {
        pinComponent.disablePin()
        pinComponent.isBiometricAuthEnabled = false
        emitState()
    }

    fun disableDuressPin() {
        pinComponent.disableDuressPin()
        emitState()
    }

    fun onSetBalanceAutoHidden(enabled: Boolean) {
        balanceAutoHideEnabled = enabled
        emitState()
        balanceHiddenManager.setBalanceAutoHidden(enabled)
    }

    fun update() {
        emitState()
    }

    fun onSetAnalytic(enabled: Boolean) {
        analyticLog = enabled
        emitState()
        localStorage.isAnalytic = enabled
        Firebase.analytics.setAnalyticsCollectionEnabled(if (!BuildConfig.DEBUG) enabled else false)
    }

    fun onSetCrashlytics(enabled: Boolean) {
        detectCrash = enabled
        emitState()
        localStorage.isDetectCrash = enabled
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = if (!BuildConfig.DEBUG) enabled else false
    }
}

data class SecuritySettingsUiState(
    val pinEnabled: Boolean,
    val biometricsEnabled: Boolean,
    val duressPinEnabled: Boolean,
    val balanceAutoHideEnabled: Boolean,
    val autoLockIntervalName: Int,
    val analyticLog: Boolean,
    val detectCrash: Boolean
)