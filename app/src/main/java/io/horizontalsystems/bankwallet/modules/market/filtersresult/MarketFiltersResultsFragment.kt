package io.horizontalsystems.bankwallet.modules.market.filtersresult

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.core.stats.StatEvent
import io.horizontalsystems.bankwallet.core.stats.StatPage
import io.horizontalsystems.bankwallet.core.stats.stat
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Loading
import io.horizontalsystems.bankwallet.modules.market.filters.MarketFiltersViewModel
import io.horizontalsystems.bankwallet.modules.market.topcoins.OptionController
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.AlertGroup
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.CoinList
import io.horizontalsystems.bankwallet.ui.compose.components.HSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.HeaderSorting
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.ListErrorView

class MarketFiltersResultsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val viewModel = getViewModel()

        if (viewModel == null) {
            navController.popBackStack()
            return
        }

        SearchResultsScreen(viewModel, navController)
    }

    private fun getViewModel(): MarketFiltersResultViewModel? {
        return try {
            val marketSearchFilterViewModel by navGraphViewModels<MarketFiltersViewModel>(R.id.marketAdvancedSearchFragment)
            val viewModel by viewModels<MarketFiltersResultViewModel> {
                MarketFiltersResultsModule.Factory(marketSearchFilterViewModel.service)
            }
            viewModel
        } catch (e: RuntimeException) {
            null
        }
    }

    override val logScreen: String
        get() = "MarketFiltersResultsFragment"
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultsScreen(
    viewModel: MarketFiltersResultViewModel,
    navController: NavController
) {

    val uiState = viewModel.uiState
    var scrollToTopAfterUpdate by rememberSaveable { mutableStateOf(false) }
    var openSortingSelector by rememberSaveable { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            AppBar(
                title = stringResource(R.string.Market_AdvancedSearch_Results),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
            )

            Crossfade(uiState.viewState, label = "") { state ->
                when (state) {
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

                                stat(
                                    page = StatPage.AdvancedSearchResults,
                                    event = StatEvent.AddToWatchlist(uid)
                                )
                            },
                            onRemoveFavorite = { uid ->
                                viewModel.onRemoveFavorite(uid)

                                stat(
                                    page = StatPage.AdvancedSearchResults,
                                    event = StatEvent.RemoveFromWatchlist(uid)
                                )
                            },
                            onCoinClick = { coinUid ->
                                val arguments = CoinFragment.Input(coinUid)
                                navController.slideFromRight(R.id.coinFragment, arguments)

                                stat(
                                    page = StatPage.AdvancedSearchResults,
                                    event = StatEvent.OpenCoin(coinUid)
                                )
                            },
                            preItems = {
                                stickyHeader {
                                    HeaderSorting(borderBottom = true, borderTop = true) {
                                        HSpacer(width = 16.dp)
                                        OptionController(
                                            uiState.sortingField.titleResId,
                                            onOptionClick = {
                                                openSortingSelector = true
                                            }
                                        )
                                        HSpacer(width = 16.dp)
                                    }
                                }
                            }
                        )
                        if (scrollToTopAfterUpdate) {
                            scrollToTopAfterUpdate = false
                        }
                    }
                }
            }

            if (openSortingSelector) {
                AlertGroup(
                    title = R.string.Market_Sort_PopupTitle,
                    select = uiState.selectSortingField,
                    onSelect = { selected ->
                        viewModel.onSelectSortingField(selected)
                        openSortingSelector = false
                        scrollToTopAfterUpdate = true
                    },
                    onDismiss = {
                        openSortingSelector = false
                    }
                )
            }

        }
    }
}
