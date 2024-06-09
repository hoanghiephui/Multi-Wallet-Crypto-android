package io.horizontalsystems.bankwallet.modules.market

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.billing.UserDataRepository
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.analytics.TrackScreenViewEvent
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.core.stats.StatEvent
import io.horizontalsystems.bankwallet.core.stats.StatPage
import io.horizontalsystems.bankwallet.core.stats.StatSection
import io.horizontalsystems.bankwallet.core.stats.stat
import io.horizontalsystems.bankwallet.core.stats.statPage
import io.horizontalsystems.bankwallet.core.stats.statSection
import io.horizontalsystems.bankwallet.core.stats.statTab
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.main.MainModule
import io.horizontalsystems.bankwallet.modules.main.MainViewModel
import io.horizontalsystems.bankwallet.modules.market.MarketModule.Tab
import io.horizontalsystems.bankwallet.modules.market.favorites.MarketFavoritesScreen
import io.horizontalsystems.bankwallet.modules.market.posts.MarketPostsScreen
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchResults
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchSection
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchViewModel
import io.horizontalsystems.bankwallet.modules.market.topcoins.TopCoins
import io.horizontalsystems.bankwallet.modules.market.toppairs.TopPairsScreen
import io.horizontalsystems.bankwallet.modules.market.topplatforms.TopPlatforms
import io.horizontalsystems.bankwallet.modules.metricchart.MetricsType
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.HSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.ScrollableTabs
import io.horizontalsystems.bankwallet.ui.compose.components.TabItem
import io.horizontalsystems.bankwallet.ui.compose.components.caption_bran
import io.horizontalsystems.bankwallet.ui.compose.components.caption_grey
import io.horizontalsystems.bankwallet.ui.compose.components.caption_lucian
import io.horizontalsystems.bankwallet.ui.compose.components.caption_remus

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    navController: NavController,
    searchViewModel: MarketSearchViewModel,
    mainViewModel: MainViewModel
) {
    val marketViewModel = viewModel<MarketViewModel>(factory = MarketModule.Factory())
    val uiState = marketViewModel.uiState
    val tabs = marketViewModel.tabs
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = mainViewModel.currentMainTab, block = {
        if (mainViewModel.currentMainTab != MainModule.MainNavigation.Market) {
            active = false
        }
    })
    
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .semantics { isTraversalGroup = true }
                .zIndex(1f)
                .fillMaxWidth()
        ) {
            DockedSearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = -1f }
                    .padding(top = 33.dp),
                query = text,
                onQueryChange = {
                    text = it
                    searchViewModel.searchByQuery(it)
                },
                onSearch = {
                    active = false
                    text = ""
                },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text(stringResource(R.string.Market_Search_Hint)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = {
                        active = false
                        navController.slideFromRight(R.id.marketSearchFragment)

                        stat(page = StatPage.Markets, event = StatEvent.Open(StatPage.MarketSearch))
                    }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = null)

                        stat(
                            page = StatPage.Markets,
                            event = StatEvent.Open(StatPage.AdvancedSearch)
                        )
                    }
                },
            ) {
                Crossfade(targetState = text.isBlank(), label = "") { isSearch ->
                    val uiSearchState = searchViewModel.uiState
                    val itemSections =
                        if (!isSearch && uiSearchState.page is MarketSearchViewModel.Page.SearchResults)
                            mapOf(
                                MarketSearchSection.SearchResults to uiSearchState.page.items
                            )
                        else if (uiSearchState.page is MarketSearchViewModel.Page.Discovery)
                            mapOf(
                                MarketSearchSection.Popular to uiSearchState.page.popular.take(6),
                            ) else mapOf()

                    MarketSearchResults(
                        uiSearchState.listId,
                        itemSections = itemSections,
                        onCoinClick = { coin, section ->
                            active = false
                            text = ""
                            uiSearchState.page
                            searchViewModel.onCoinOpened(coin)
                            navController.slideFromRight(
                                R.id.coinFragment,
                                CoinFragment.Input(coin.uid)
                            )
                            stat(
                                page = StatPage.MarketSearch,
                                section = section.statSection,
                                event = StatEvent.OpenCoin(coin.uid)
                            )
                        }
                    ) { favorited, coinUid ->
                        searchViewModel.onFavoriteClick(favorited, coinUid)
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 92.dp)
                .background(ComposeAppTheme.colors.tyler)
        ) {
            MetricsBoard(navController, uiState.marketOverviewItems)
            HorizontalDivider(
                color = ComposeAppTheme.colors.steel10,
                thickness = 1.dp
            )
            TabsSection(navController, tabs, uiState.selectedTab) { tab ->
                marketViewModel.onSelect(tab)
            }
        }
    }

    TrackScreenViewEvent("MarketScreen")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabsSection(
    navController: NavController,
    tabs: Array<Tab>,
    selectedTab: Tab,
    onTabClick: (Tab) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal) { tabs.size }

    LaunchedEffect(key1 = selectedTab, block = {
        pagerState.scrollToPage(selectedTab.ordinal)

        stat(page = StatPage.Markets, event = StatEvent.SwitchTab(selectedTab.statTab))
    })
    val tabItems = tabs.map {
        TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
    }

    ScrollableTabs(tabItems) {
        onTabClick(it)
    }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false
    ) { page ->
        when (tabs[page]) {
            Tab.Coins -> {
                TopCoins(onCoinClick = { onCoinClick(it, navController) })
            }

            Tab.Watchlist -> {
                MarketFavoritesScreen(navController)
            }

            Tab.Posts -> {
                MarketPostsScreen()
            }

            Tab.Platform -> {
                TopPlatforms(navController)
            }

            Tab.Pairs -> {
                TopPairsScreen()
            }
        }
    }
}

@Composable
fun MetricsBoard(
    navController: NavController,
    marketOverviewItems: List<MarketModule.MarketOverviewViewItem>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(ComposeAppTheme.colors.tyler)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HSpacer(4.dp)
        marketOverviewItems.forEach { item ->
            Row(
                modifier = Modifier
                    .clickable {
                        openMetricsPage(item.metricsType, navController)
                    }
                    .padding(8.dp)
            ) {
                HSpacer(12.dp)
                caption_grey(text = item.title)
                HSpacer(4.dp)
                caption_bran(text = item.value)
                HSpacer(4.dp)
                if (item.changePositive) {
                    caption_remus(text = item.change)
                } else {
                    caption_lucian(text = item.change)
                }
                HSpacer(12.dp)
            }
        }
        HSpacer(4.dp)
    }
}

private fun openMetricsPage(metricsType: MetricsType, navController: NavController) {
    when (metricsType) {
        MetricsType.TvlInDefi -> {
            navController.slideFromBottom(R.id.tvlFragment)
        }

        MetricsType.Etf -> {
            navController.slideFromBottom(R.id.etfFragment)
        }

        else -> {
            navController.slideFromBottom(R.id.metricsPageFragment, metricsType)
        }
    }

    stat(page = StatPage.Markets, event = StatEvent.Open(metricsType.statPage))
}

private fun onCoinClick(coinUid: String, navController: NavController) {
    val arguments = CoinFragment.Input(coinUid)

    navController.slideFromRight(R.id.coinFragment, arguments)

    stat(page = StatPage.Markets, section = StatSection.Coins, event = StatEvent.OpenCoin(coinUid))
}
