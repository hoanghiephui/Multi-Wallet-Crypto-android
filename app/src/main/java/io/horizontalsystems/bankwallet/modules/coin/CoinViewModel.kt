package io.horizontalsystems.bankwallet.modules.coin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.Clearable
import io.horizontalsystems.bankwallet.core.ILocalStorage
import io.horizontalsystems.bankwallet.core.managers.SubscriptionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val coinUid: String = checkNotNull(savedStateHandle[CoinFragment.COIN_UID_KEY])
    val fullCoin = App.marketKit.fullCoins(coinUids = listOf(coinUid)).first()
    private val service: CoinService = CoinService(fullCoin, App.marketFavoritesManager)
    private val clearables: List<Clearable> = listOf(service)
    private val localStorage: ILocalStorage = App.localStorage
    private val subscriptionManager: SubscriptionManager = App.subscriptionManager

    val tabs = CoinModule.Tab.values()

    val isWatchlistEnabled = localStorage.marketsTabEnabled
    var isFavorite by mutableStateOf(false)
        private set
    var successMessage by mutableStateOf<Int?>(null)
        private set

    private var subscriptionInfoShown: Boolean = false

    init {
        viewModelScope.launch {
            val isFavoriteFlow: Flow<Boolean> = service.isFavorite.asFlow()
            isFavoriteFlow.collect {
                isFavorite = it
            }
        }
    }

    override fun onCleared() {
        clearables.forEach(Clearable::clear)
    }

    fun onFavoriteClick() {
        service.favorite()
        successMessage = R.string.Hud_Added_To_Watchlist
    }

    fun onUnfavoriteClick() {
        service.unfavorite()
        successMessage = R.string.Hud_Removed_from_Watchlist
    }

    fun onSuccessMessageShown() {
        successMessage = null
    }

    fun shouldShowSubscriptionInfo(): Boolean {
        return !subscriptionManager.hasSubscription() && !subscriptionInfoShown
    }

    fun subscriptionInfoShown() {
        subscriptionInfoShown = true
    }

}
