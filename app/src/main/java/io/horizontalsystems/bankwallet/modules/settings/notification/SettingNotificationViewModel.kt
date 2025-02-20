package io.horizontalsystems.bankwallet.modules.settings.notification

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.ILocalStorage
import io.horizontalsystems.bankwallet.core.ViewModelUiState
import io.horizontalsystems.bankwallet.worker.Sync.initializeNews
import io.horizontalsystems.bankwallet.worker.Sync.initializePrice

class SettingNotificationViewModel(
    private val localStorage: ILocalStorage,
) : ViewModelUiState<NotificationSettingsUiState>() {
    private var isShowNotificationPrice = localStorage.isShowNotificationPrice
    private var isShowNotificationNews = localStorage.isShowNotificationNews
    override fun createState(): NotificationSettingsUiState {
        return NotificationSettingsUiState(
            isShowNotificationPrice = isShowNotificationPrice,
            isShowNotificationNews = isShowNotificationNews,
        )
    }

    fun onSetNotificationPrice(
        value: Boolean,
        context: Context
    ) {
        localStorage.isShowNotificationPrice = value
        isShowNotificationPrice = value
        emitState()
        initializePrice(context, value)
    }

    fun onSetNotificationNews(
        value: Boolean,
        context: Context
    ) {
        localStorage.isShowNotificationNews = value
        isShowNotificationNews = value
        emitState()
        initializeNews(context, value)
    }
}

data class NotificationSettingsUiState(
    val isShowNotificationPrice: Boolean,
    val isShowNotificationNews: Boolean,
)

class Factory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingNotificationViewModel(
            App.localStorage,
        ) as T
    }
}