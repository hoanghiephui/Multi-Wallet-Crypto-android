package io.horizontalsystems.bankwallet.modules.market.topcoins

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.AdType
import io.horizontalsystems.bankwallet.core.AdViewState
import io.horizontalsystems.bankwallet.core.MaxTemplateNativeAdViewComposable
import io.horizontalsystems.bankwallet.core.stats.StatEvent
import io.horizontalsystems.bankwallet.core.stats.StatPage
import io.horizontalsystems.bankwallet.core.stats.StatSection
import io.horizontalsystems.bankwallet.core.stats.stat
import io.horizontalsystems.bankwallet.core.stats.statMarketTop
import io.horizontalsystems.bankwallet.core.stats.statPeriod
import io.horizontalsystems.bankwallet.core.stats.statSortType
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Loading
import io.horizontalsystems.bankwallet.modules.market.SortingField
import io.horizontalsystems.bankwallet.modules.market.TopMarket
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.Select
import io.horizontalsystems.bankwallet.ui.compose.components.AlertGroup
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonSecondaryWithIcon
import io.horizontalsystems.bankwallet.ui.compose.components.CoinList
import io.horizontalsystems.bankwallet.ui.compose.components.HSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.HeaderSorting
import io.horizontalsystems.bankwallet.ui.compose.components.ListErrorView
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopCoins(
    onCoinClick: (String) -> Unit,
    viewModel: MarketTopCoinsViewModel = viewModel(
        factory = MarketTopCoinsModule.Factory(
            TopMarket.Top100,
            SortingField.TopGainers,
        )
    ),
    nativeAd: AdViewState
) {

    var openSortingSelector by rememberSaveable { mutableStateOf(false) }
    var openTopSelector by rememberSaveable { mutableStateOf(false) }
    var openPeriodSelector by rememberSaveable { mutableStateOf(false) }
    var scrollToTopAfterUpdate by rememberSaveable { mutableStateOf(false) }

    val uiState = viewModel.uiState

    HSSwipeRefresh(
        refreshing = uiState.isRefreshing,
        onRefresh = {
            viewModel.refresh()

            stat(page = StatPage.Markets, section = StatSection.Coins, event = StatEvent.Refresh)
        }
    ) {
        Crossfade(uiState.viewState, label = "",
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
        ) { viewState ->
            when (viewState) {
                ViewState.Loading -> {
                    Loading()
                }

                is ViewState.Error -> {
                    ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                }

                ViewState.Success -> {
                    CoinList(
                        items = uiState.viewItems,
                        scrollToTop = scrollToTopAfterUpdate,
                        onAddFavorite = { uid ->
                            viewModel.onAddFavorite(uid)

                            stat(page = StatPage.Markets, section = StatSection.Coins, event = StatEvent.AddToWatchlist(uid))

                        },
                        onRemoveFavorite = { uid ->
                            viewModel.onRemoveFavorite(uid)

                            stat(page = StatPage.Markets, section = StatSection.Coins, event = StatEvent.RemoveFromWatchlist(uid))
                        },
                        onCoinClick = onCoinClick,
                        preItems = {
                            stickyHeader {
                                HeaderSorting(
                                    borderBottom = true,
                                ) {
                                    HSpacer(width = 16.dp)
                                    OptionController(
                                        uiState.sortingField.titleResId,
                                        onOptionClick = {
                                            openSortingSelector = true
                                        }
                                    )
                                    HSpacer(width = 12.dp)
                                    OptionController(
                                        uiState.topMarket.titleResId,
                                        onOptionClick = {
                                            openTopSelector = true
                                        }
                                    )
                                    HSpacer(width = 12.dp)
                                    OptionController(
                                        uiState.timeDuration.titleResId,
                                        onOptionClick = {
                                            openPeriodSelector = true
                                        }
                                    )
                                    HSpacer(width = 16.dp)
                                }
                            }
                        },
                        preAdsItem = {
                            item {
                                VSpacer(12.dp)
                                MaxTemplateNativeAdViewComposable(nativeAd, AdType.SMALL)
                                VSpacer(8.dp)
                            }
                        }
                    )
                    if (scrollToTopAfterUpdate) {
                        scrollToTopAfterUpdate = false
                    }
                }
            }
        }
    }

    if (openSortingSelector) {
        AlertGroup(
            title = R.string.Market_Sort_PopupTitle,
            select = Select(uiState.sortingField, viewModel.sortingFields),
            onSelect = { selected ->
                viewModel.onSelectSortingField(selected)
                openSortingSelector = false
                scrollToTopAfterUpdate = true

                stat(page = StatPage.Markets, section = StatSection.Coins, event = StatEvent.SwitchSortType(selected.statSortType))
            },
            onDismiss = {
                openSortingSelector = false
            }
        )
    }
    if (openTopSelector) {
        AlertGroup(
            title = R.string.Market_Tab_Coins,
            select = Select(uiState.topMarket, viewModel.topMarkets),
            onSelect = {
                viewModel.onSelectTopMarket(it)
                openTopSelector = false
                scrollToTopAfterUpdate = true

                stat(page = StatPage.Markets, section = StatSection.Coins, event = StatEvent.SwitchMarketTop(it.statMarketTop))
            },
            onDismiss = {
                openTopSelector = false
            }
        )
    }
    if (openPeriodSelector) {
        AlertGroup(
            title = R.string.CoinPage_Period,
            select = Select(uiState.timeDuration, viewModel.periods),
            onSelect = { selected ->
                viewModel.onSelectPeriod(selected)
                openPeriodSelector = false
                scrollToTopAfterUpdate = true

                stat(page = StatPage.Markets, section = StatSection.Coins, event = StatEvent.SwitchPeriod(selected.statPeriod))
            },
            onDismiss = {
                openPeriodSelector = false
            }
        )
    }
}

@Composable
fun OptionController(
    label: Int,
    onOptionClick: () -> Unit
) {
    ButtonSecondaryWithIcon(
        modifier = Modifier.height(28.dp),
        onClick = onOptionClick,
        title = stringResource(label),
        iconRight = painterResource(R.drawable.ic_down_arrow_20),
    )
}
