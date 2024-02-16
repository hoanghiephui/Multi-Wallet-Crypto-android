package io.horizontalsystems.bankwallet.modules.market

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.android.billing.UserDataRepository
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.analytics.TrackScreenViewEvent
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.main.MainModule
import io.horizontalsystems.bankwallet.modules.main.MainViewModel
import io.horizontalsystems.bankwallet.modules.market.favorites.MarketFavoritesScreen
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewModule
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewScreen
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewViewModel
import io.horizontalsystems.bankwallet.modules.market.posts.MarketPostsScreen
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchResults
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchViewModel
import io.horizontalsystems.bankwallet.ui.compose.NiaTab
import io.horizontalsystems.bankwallet.ui.compose.NiaTabRow
import java.util.Optional

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    navController: NavController,
    searchViewModel: MarketSearchViewModel,
    userDataRepository: UserDataRepository,
    mainViewModel: MainViewModel
) {
    val marketViewModel = viewModel<MarketViewModel>(factory = MarketModule.Factory())
    val viewModel: MarketOverviewViewModel = viewModel(factory = MarketOverviewModule.Factory(userDataRepository))
    val tabs = marketViewModel.tabs
    val selectedTab = marketViewModel.selectedTab
    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal) { tabs.size }
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
                    }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = null)
                    }
                },
            ) {
                Crossfade(targetState = text.isBlank(), label = "") { isSearch ->
                    val uiState = searchViewModel.uiState
                    val itemSections =
                        if (!isSearch && uiState.page is MarketSearchViewModel.Page.SearchResults)
                            mapOf(
                                Optional.ofNullable<String>(null) to uiState.page.items
                            )
                        else if (uiState.page is MarketSearchViewModel.Page.Discovery)
                            mapOf(
                                Optional.of(stringResource(R.string.Market_Search_Sections_PopularTitle)) to uiState.page.popular.take(6)
                            ) else mapOf()

                    MarketSearchResults(
                        uiState.listId,
                        itemSections = itemSections,
                        onCoinClick = { coin ->
                            active = false
                            text = ""
                            uiState.page
                            searchViewModel.onCoinOpened(coin)
                            navController.slideFromRight(
                                R.id.coinFragment,
                                CoinFragment.Input(coin.uid, "market_search")
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
                    MarketModule.Tab.Overview -> MarketOverviewScreen(navController, viewModel) {
                        active = false
                    }
                    MarketModule.Tab.Posts -> MarketPostsScreen()
                    MarketModule.Tab.Watchlist -> MarketFavoritesScreen(navController)
                }
            }
        }
    }

    TrackScreenViewEvent("MarketScreen")


}
