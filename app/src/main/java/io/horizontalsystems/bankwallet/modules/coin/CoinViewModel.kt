package io.horizontalsystems.bankwallet.modules.coin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.Clearable
import io.horizontalsystems.bankwallet.core.ILocalStorage
import io.horizontalsystems.subscriptions.core.NoAds
import io.horizontalsystems.subscriptions.core.UserSubscriptionManager
import io.horizontalsystems.subscriptions.core.Watchlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow

class CoinViewModel(
    private val service: CoinService,
    private val clearables: List<Clearable>,
    localStorage: ILocalStorage,
) : ViewModel() {

    val tabs = CoinModule.Tab.entries.toTypedArray()
    val fullCoin by service::fullCoin

    val isWatchlistEnabled = localStorage.marketsTabEnabled
    var isFavorite by mutableStateOf(false)
        private set
    var successMessage by mutableStateOf<Int?>(null)
        private set

    val purchaseStateUpdated get() = UserSubscriptionManager.isActionAllowed(Watchlist)

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

}
