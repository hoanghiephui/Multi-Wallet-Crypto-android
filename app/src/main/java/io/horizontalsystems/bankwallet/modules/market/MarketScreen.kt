package io.horizontalsystems.bankwallet.modules.market

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Loading
import io.horizontalsystems.bankwallet.modules.market.favorites.MarketFavoritesScreen
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewModule
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewScreen
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewViewModel
import io.horizontalsystems.bankwallet.modules.market.overview.ui.MarketCoinWithBackground
import io.horizontalsystems.bankwallet.modules.market.posts.MarketPostsScreen
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchModule
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchResults
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchViewModel
import io.horizontalsystems.bankwallet.ui.compose.NiaTab
import io.horizontalsystems.bankwallet.ui.compose.NiaTabRow
import io.horizontalsystems.bankwallet.ui.compose.components.ListEmptyView
import io.horizontalsystems.bankwallet.ui.compose.components.ListErrorView

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketScreen(
    navController: NavController,
    searchViewModel: MarketSearchViewModel
) {
    val marketViewModel = viewModel<MarketViewModel>(factory = MarketModule.Factory())
    val viewModel: MarketOverviewViewModel = viewModel(factory = MarketOverviewModule.Factory())
    val tabs = marketViewModel.tabs
    val selectedTab = marketViewModel.selectedTab
    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal) { tabs.size }
    DockedSearchBar(
        navController = navController,
        searchViewModel = searchViewModel,
        viewModel = viewModel,
        onSearchTextChanged = {
            searchViewModel.searchByQuery(it)
        },
        content = {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 92.dp)
            ) {

                LaunchedEffect(key1 = selectedTab, block = {
                    pagerState.scrollToPage(selectedTab.ordinal)
                })


                NiaTabRow(selectedTabIndex = selectedTab.ordinal) {
                    tabs.forEach { title ->
                        NiaTab(
                            selected = title == selectedTab,
                            onClick = { marketViewModel.onSelect(title) },
                            text = { Text(text = stringResource(id = title.titleResId)) },
                        )
                    }
                }
                // our list with build in nested scroll support that will notify us about its scroll
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false,
                ) { page ->
                    viewModel.showSearchBar.value = false
                    when (tabs[page]) {
                        MarketModule.Tab.Overview -> MarketOverviewScreen(navController, viewModel)
                        MarketModule.Tab.Posts -> MarketPostsScreen()
                        MarketModule.Tab.Watchlist -> MarketFavoritesScreen(navController)
                    }
                }
            }
        })

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DockedSearchBar(
    content: @Composable () -> Unit,
    navController: NavController,
    onSearchTextChanged: (String) -> Unit = {},
    viewModel: MarketOverviewViewModel,
    searchViewModel: MarketSearchViewModel
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
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
                    onSearchTextChanged.invoke(text)
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
                    }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = null)
                    }
                },
            ) {
                Crossfade(targetState = text.isBlank(), label = "") { isSearch ->
                    if (!isSearch) {
                        val viewState = searchViewModel.viewState
                        Crossfade(viewState, label = "SearchView") { state ->
                            when (state) {
                                ViewState.Loading -> {
                                    Loading()
                                }

                                is ViewState.Error -> {
                                    ListErrorView(
                                        stringResource(R.string.SyncError),
                                        onClick = {
                                            searchViewModel.refresh()
                                            active = false
                                            text = ""
                                        }
                                    )
                                }

                                ViewState.Success -> {
                                    when (val itemsData = searchViewModel.itemsData) {
                                        is MarketSearchModule.Data.SearchResult -> {
                                            if (itemsData.coinItems.isEmpty()) {
                                                ListEmptyView(
                                                    text = stringResource(R.string.EmptyResults),
                                                    icon = R.drawable.ic_not_found
                                                )
                                            } else {
                                                MarketSearchResults(
                                                    coinResult = itemsData.coinItems,
                                                    onCoinClick = { coin ->
                                                        val arguments =
                                                            CoinFragment.prepareParams(coin.uid)
                                                        navController.slideFromRight(
                                                            R.id.coinFragment,
                                                            arguments
                                                        )
                                                        active = false
                                                        text = ""
                                                    }
                                                ) { favorite, coinUid ->
                                                    searchViewModel.onFavoriteClick(
                                                        favorite,
                                                        coinUid
                                                    )
                                                }
                                            }
                                        }

                                        else -> Unit
                                    }

                                }
                            }
                        }
                    } else {
                        val viewState by viewModel.viewStateLiveData.observeAsState()
                        val viewItem by viewModel.viewItem.observeAsState()
                        Crossfade(viewState, label = "MarketOverviewScreen") { state ->
                            when (state) {
                                ViewState.Loading -> {
                                    Loading()
                                }

                                is ViewState.Error -> {
                                    ListErrorView(
                                        stringResource(R.string.SyncError),
                                        onClick = {
                                            viewModel.onErrorClick()
                                            active = false
                                            text = ""
                                        }
                                    )
                                }

                                ViewState.Success -> {
                                    viewItem?.let { viewItem ->
                                        val rememberLazyListState = rememberLazyListState()
                                        val item = viewItem.boards.first().marketViewItems
                                        LazyColumn(
                                            state = rememberLazyListState,
                                            modifier = Modifier.fillMaxWidth(),
                                            contentPadding = PaddingValues(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            items(item) {
                                                MarketCoinWithBackground(
                                                    it,
                                                    navController,
                                                    click = {
                                                        active = false
                                                        text = ""
                                                    })
                                            }
                                        }
                                        LaunchedEffect(rememberLazyListState) {
                                            snapshotFlow { rememberLazyListState.isScrollInProgress }
                                                .collect { keyboardController?.hide() }
                                        }
                                    }
                                }

                                else -> Unit
                            }
                        }
                    }
                }
            }
        }

        content.invoke()
    }
}
