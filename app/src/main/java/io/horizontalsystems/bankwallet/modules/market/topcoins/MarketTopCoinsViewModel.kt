package io.horizontalsystems.bankwallet.modules.market.topcoins

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.core.subscribeIO
import io.horizontalsystems.bankwallet.entities.DataState
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.market.ImageSource
import io.horizontalsystems.bankwallet.modules.market.MarketField
import io.horizontalsystems.bankwallet.modules.market.MarketModule
import io.horizontalsystems.bankwallet.modules.market.MarketViewItem
import io.horizontalsystems.bankwallet.modules.market.SortingField
import io.horizontalsystems.bankwallet.modules.market.TopMarket
import io.horizontalsystems.bankwallet.modules.market.category.MarketItemWrapper
import io.horizontalsystems.bankwallet.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketTopCoinsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val topMarket: TopMarket? = savedStateHandle[MarketTopCoinsFragment.topMarketKey]
    private val sortingField: SortingField? = savedStateHandle[MarketTopCoinsFragment.sortingFieldKey]
    private var marketField: MarketField = savedStateHandle[MarketTopCoinsFragment.marketFieldKey] ?: MarketTopCoinsModule.defaultMarketField
    private val topMarketsRepository = MarketTopMoversRepository(App.marketKit)
    val service = MarketTopCoinsService(
        topMarketsRepository,
        App.currencyManager,
        App.marketFavoritesManager,
        topMarket ?: MarketTopCoinsModule.defaultTopMarket,
        sortingField ?: MarketTopCoinsModule.defaultSortingField
    )
    private val disposables = CompositeDisposable()
    private val marketFields = MarketField.entries
    private var marketItems: List<MarketItemWrapper> = listOf()

    val headerLiveData = MutableLiveData<MarketModule.Header>()
    val menuLiveData = MutableLiveData<Menu>()
    val viewItemsLiveData = MutableLiveData<List<MarketViewItem>>()
    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val selectorDialogStateLiveData = MutableLiveData<SelectorDialogState>()

    init {
        syncHeader()
        syncMenu()

        service.stateObservable
            .subscribeIO {
                syncState(it)
            }.let {
                disposables.add(it)
            }

        service.start()
    }

    private fun syncState(state: DataState<List<MarketItemWrapper>>) {
        state.viewState?.let {
            viewStateLiveData.postValue(it)
        }

        state.dataOrNull?.let {
            marketItems = it

            syncMarketViewItems()
        }

        syncMenu()
    }

    private fun syncHeader() {
        headerLiveData.postValue(
            MarketModule.Header(
                Translator.getString(R.string.Market_Category_TopCoins),
                Translator.getString(R.string.Market_Category_TopCoins_Description),
                ImageSource.Local(R.drawable.ic_top_coins)
            )
        )
    }

    private fun syncMenu() {
        menuLiveData.postValue(
            Menu(
                Select(service.sortingField, service.sortingFields),
                Select(service.topMarket, service.topMarkets),
                Select(marketField, marketFields)
            )
        )
    }

    private fun syncMarketViewItems() {
        viewItemsLiveData.postValue(
            marketItems.map {
                MarketViewItem.create(it.marketItem, marketField, it.favorited)
            }
        )
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    fun onSelectSortingField(sortingField: SortingField) {
        service.setSortingField(sortingField)
        selectorDialogStateLiveData.postValue(SelectorDialogState.Closed)
    }

    fun onSelectTopMarket(topMarket: TopMarket) {
        service.setTopMarket(topMarket)
    }

    fun onSelectMarketField(marketField: MarketField) {
        this.marketField = marketField

        syncMarketViewItems()
        syncMenu()
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    override fun onCleared() {
        service.stop()
        disposables.clear()
    }

    fun onSelectorDialogDismiss() {
        selectorDialogStateLiveData.postValue(SelectorDialogState.Closed)
    }

    fun showSelectorMenu() {
        selectorDialogStateLiveData.postValue(
            SelectorDialogState.Opened(Select(service.sortingField, service.sortingFields))
        )
    }

    fun onAddFavorite(coinUid: String) {
        service.addFavorite(coinUid)
    }

    fun onRemoveFavorite(coinUid: String) {
        service.removeFavorite(coinUid)
    }
}
