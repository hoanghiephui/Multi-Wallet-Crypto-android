package io.horizontalsystems.bankwallet.modules.market

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.BuildConfig
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.AdNativeUiState
import io.horizontalsystems.bankwallet.analytics.TrackScreenViewEvent
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.core.stats.StatEvent
import io.horizontalsystems.bankwallet.core.stats.StatPage
import io.horizontalsystems.bankwallet.core.stats.StatSection
import io.horizontalsystems.bankwallet.core.stats.stat
import io.horizontalsystems.bankwallet.core.stats.statPage
import io.horizontalsystems.bankwallet.core.stats.statTab
import io.horizontalsystems.bankwallet.entities.Currency
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.main.MainModule
import io.horizontalsystems.bankwallet.modules.main.MainViewModel
import io.horizontalsystems.bankwallet.modules.market.MarketModule.Tab
import io.horizontalsystems.bankwallet.modules.market.favorites.MarketFavoritesScreen
import io.horizontalsystems.bankwallet.modules.market.posts.MarketPostsScreen
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchViewModel
import io.horizontalsystems.bankwallet.modules.market.topcoins.TopCoins
import io.horizontalsystems.bankwallet.modules.market.toppairs.TopPairsScreen
import io.horizontalsystems.bankwallet.modules.market.topplatforms.TopPlatforms
import io.horizontalsystems.bankwallet.modules.market.topsectors.TopSectorsScreen
import io.horizontalsystems.bankwallet.modules.metricchart.MetricsType
import io.horizontalsystems.bankwallet.rememberAdNativeView
import io.horizontalsystems.bankwallet.ui.CollapsingAppBarNestedScrollConnection
import io.horizontalsystems.bankwallet.ui.CollapsingLayout
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.components.ScrollableTabs
import io.horizontalsystems.bankwallet.ui.compose.components.TabItem
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.caption_bran
import io.horizontalsystems.bankwallet.ui.compose.components.caption_grey
import io.horizontalsystems.bankwallet.ui.compose.components.caption_lucian
import io.horizontalsystems.bankwallet.ui.compose.components.caption_remus
import io.horizontalsystems.bankwallet.ui.compose.components.micro_grey
import io.horizontalsystems.marketkit.models.MarketGlobal
import java.math.BigDecimal


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
    var expanded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = mainViewModel.currentMainTab, block = {
        if (mainViewModel.currentMainTab != MainModule.MainNavigation.Market) {
            expanded = false
        }
    })
    val (adState, reloadAd) = rememberAdNativeView(BuildConfig.HOME_MARKET_NATIVE, marketViewModel)
    var isRefreshing by remember {
        mutableStateOf(false)
    }
    var onRefresh: (() -> Unit)? = null

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        HSSwipeRefresh(
            modifier = Modifier.statusBarsPadding(),
            refreshing = isRefreshing,
            topPadding = 0,
            onRefresh = {
                onRefresh?.invoke()
                reloadAd()
            }
        ) {
            CollapsingLayout(
                expandedContent = { modifier ->
                    Crossfade(uiState.marketGlobal, label = "") {
                        MetricsBoard(navController, it, uiState.currency)
                    }
                },
                collapsedContent = { modifier ->

                }
            ) { modifier ->
                TabsSection(
                    navController = navController,
                    tabs = tabs,
                    selectedTab = uiState.selectedTab,
                    nativeAd = adState,
                    isRefreshing = {
                        isRefreshing = it
                    },
                    onSetRefreshCallback = { refreshCallback ->
                        onRefresh = refreshCallback
                    },
                    onTabClick = { tab ->
                        marketViewModel.onSelect(tab)
                    }
                )
            }
        }
    }


    /*Box(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()) {
        Box(
            Modifier
                .semantics { isTraversalGroup = true }
                .zIndex(1f)
                .fillMaxWidth()
        ) {

            DockedSearchBar(
                modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
                    .semantics {
                        traversalIndex = 0f
                    },
                inputField = {
                    SearchBarDefaults.InputField(
                        onSearch = {
                            expanded = false
                            text = ""
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text(stringResource(R.string.Market_Search_Hint)) },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = {
                                expanded = false
                                navController.slideFromRight(R.id.marketSearchFragment)

                                stat(
                                    page = StatPage.Markets,
                                    event = StatEvent.Open(StatPage.MarketSearch)
                                )
                            }) {
                                Icon(Icons.Rounded.MoreVert, contentDescription = null)

                                stat(
                                    page = StatPage.Markets,
                                    event = StatEvent.Open(StatPage.AdvancedSearch)
                                )
                            }
                        },
                        query = text,
                        onQueryChange = {
                            text = it
                            searchViewModel.searchByQuery(it)
                        },
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
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
                        modifier = Modifier,
                        uiSearchState.listId,
                        itemSections = itemSections,
                        onCoinClick = { coin, section ->
                            expanded = false
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
                .padding(top = 60.dp)
                .background(ComposeAppTheme.colors.tyler)
        ) {
            Crossfade(uiState.marketGlobal, label = "") {
                MetricsBoard(navController, it, uiState.currency)
            }
            HorizontalDivider(
                color = ComposeAppTheme.colors.steel10,
                thickness = 1.dp
            )
            TabsSection(
                navController = navController,
                tabs = tabs,
                selectedTab = uiState.selectedTab,
                nativeAd = adState
            ) { tab ->
                marketViewModel.onSelect(tab)
            }
        }
    }*/

    TrackScreenViewEvent("MarketScreen")
}

@Composable
fun TabsSection(
    navController: NavController,
    tabs: Array<Tab>,
    selectedTab: Tab,
    nativeAd: AdNativeUiState,
    onTabClick: (Tab) -> Unit,
    isRefreshing: (Boolean) -> Unit,
    onSetRefreshCallback: (refresh: () -> Unit) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal) { tabs.size }

    LaunchedEffect(key1 = selectedTab, block = {
        pagerState.scrollToPage(selectedTab.ordinal)

        stat(page = StatPage.Markets, event = StatEvent.SwitchTab(selectedTab.statTab))
    })
    val tabItems = tabs.map {
        TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
    }

    ScrollableTabs(
        modifier = Modifier,
        tabs = tabItems
    ) {
        onTabClick(it)
    }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false
    ) { page ->
        when (tabs[page]) {
            Tab.Coins -> TopCoins(
                onCoinClick = { onCoinClick(it, navController) },
                nativeAd = nativeAd,
                isRefreshing = isRefreshing,
                onSetRefreshCallback = onSetRefreshCallback
            )

            Tab.Watchlist -> MarketFavoritesScreen(
                navController = navController,
                isRefreshing = isRefreshing,
                onSetRefreshCallback = onSetRefreshCallback
            )

            Tab.Posts -> MarketPostsScreen(
                isRefreshing = isRefreshing,
                onSetRefreshCallback = onSetRefreshCallback
            )

            Tab.Platform -> TopPlatforms(
                navController = navController,
                isRefreshing = isRefreshing,
                onSetRefreshCallback = onSetRefreshCallback
            )

            Tab.Pairs -> TopPairsScreen(
                isRefreshing = isRefreshing,
                onSetRefreshCallback = onSetRefreshCallback
            )

            Tab.Sectors -> TopSectorsScreen(
                navController = navController,
                isRefreshing = isRefreshing,
                onSetRefreshCallback = onSetRefreshCallback
            )
        }
    }
}

private fun formatFiatShortened(value: BigDecimal, symbol: String): String {
    return App.numberFormatter.formatFiatShort(value, symbol, 2)
}

private fun getDiff(it: BigDecimal): String {
    return App.numberFormatter.format(it.abs(), 0, 2, "", "%")
}


@Composable
fun MetricsBoard(
    navController: NavController,
    marketGlobal: MarketGlobal?,
    currency: Currency
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .background(ComposeAppTheme.colors.lawrence)
    ) {
        MarketTotalCard(
            title = stringResource(R.string.MarketGlobalMetrics_TotalMarketCap),
            value = marketGlobal?.marketCap,
            changePercentage = marketGlobal?.marketCapChange,
            currency = currency,
            onClick = {
                openMetricsPage(MetricsType.TotalMarketCap, navController)
            }
        )

        VDivider()

        MarketTotalCard(
            title = stringResource(R.string.MarketGlobalMetrics_Volume),
            value = marketGlobal?.volume,
            changePercentage = marketGlobal?.volumeChange,
            currency = currency,
            onClick = {
                openMetricsPage(MetricsType.Volume24h, navController)
            }
        )

        VDivider()

        MarketTotalCard(
            title = stringResource(R.string.MarketGlobalMetrics_TvlInDefi),
            value = marketGlobal?.tvl,
            changePercentage = marketGlobal?.tvlChange,
            currency = currency,
            onClick = {
                openMetricsPage(MetricsType.TvlInDefi, navController)
            }
        )

        VDivider()

        MarketTotalCard(
            title = stringResource(R.string.MarketGlobalMetrics_EtfInflow),
            value = marketGlobal?.etfTotalInflow,
            changeFiat = marketGlobal?.etfDailyInflow,
            currency = currency,
            onClick = {
                openMetricsPage(MetricsType.Etf, navController)
            }
        )
    }
}

@Composable
private fun VDivider() {
    Box(
        Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(color = ComposeAppTheme.colors.steel10)
    )
}

@Composable
private fun RowScope.MarketTotalCard(
    title: String,
    value: BigDecimal?,
    changePercentage: BigDecimal? = null,
    changeFiat: BigDecimal? = null,
    currency: Currency,
    onClick: () -> Unit,
) {
    val changeStr: String?
    val changePositive: Boolean?

    if (changePercentage != null) {
        changeStr = getDiff(changePercentage)
        changePositive = changePercentage > BigDecimal.ZERO
    } else if (changeFiat != null) {
        changeStr = formatFiatShortened(changeFiat, currency.symbol)
        changePositive = changeFiat > BigDecimal.ZERO
    } else {
        changeStr = null
        changePositive = null
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .padding(12.dp)
            .clickable(onClick = onClick)
    ) {
        micro_grey(
            text = title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        VSpacer(4.dp)
        caption_bran(
            text = value?.let { formatFiatShortened(it, currency.symbol) } ?: "---",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        VSpacer(4.dp)

        if (changeStr == null || changePositive == null) {
            caption_grey(
                text = "---",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        } else if (changePositive) {
            caption_remus(
                text = "+$changeStr",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        } else {
            caption_lucian(
                text = "-$changeStr",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
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

data class AdsState(
    val loadAds: (Context) -> Unit
)

@Composable
fun rememberAdsState(
    loadAds: (Context) -> Unit
): AdsState {
    val context = LocalContext.current
    LaunchedEffect(key1 = BuildConfig.HOME_MARKET_NATIVE) {
        loadAds(context)
    }
    return remember(
        loadAds,
        context
    ) {
        AdsState(loadAds)
    }
}
