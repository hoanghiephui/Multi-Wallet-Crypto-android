package io.horizontalsystems.bankwallet.modules.market

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.market.favorites.MarketFavoritesScreen
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewScreen
import io.horizontalsystems.bankwallet.modules.market.posts.MarketPostsScreen
import io.horizontalsystems.bankwallet.ui.compose.NiaTab
import io.horizontalsystems.bankwallet.ui.compose.NiaTabRow
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(navController: NavController) {
    val marketViewModel = viewModel<MarketViewModel>(factory = MarketModule.Factory())
    val tabs = marketViewModel.tabs
    val selectedTab = marketViewModel.selectedTab

    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal) { tabs.size }

    val toolbarHeight = 120.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // try to consume before LazyColumn to collapse toolbar if needed, hence pre-scroll
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.floatValue + delta
                toolbarOffsetHeightPx.floatValue = newOffset.coerceIn(-toolbarHeightPx, 0f)
                // here's the catch: let's pretend we consumed 0 in any case, since we want
                // LazyColumn to scroll anyway for good UX
                // We're basically watching scroll without taking it
                return Offset.Zero
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            // attach as a parent to the nested scroll system
            .nestedScroll(nestedScrollConnection)
    ) {
        // our list with build in nested scroll support that will notify us about its scroll
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
        ) { page ->
            when (tabs[page]) {
                MarketModule.Tab.Overview -> MarketOverviewScreen(navController)
                MarketModule.Tab.Posts -> MarketPostsScreen()
                MarketModule.Tab.Watchlist -> MarketFavoritesScreen(navController)
            }
        }
        LaunchedEffect(key1 = selectedTab, block = {
            pagerState.scrollToPage(selectedTab.ordinal)
        })
        Column(
            modifier = Modifier
                .height(toolbarHeight)
                .offset {
                    IntOffset(
                        x = 0,
                        y = toolbarOffsetHeightPx.floatValue.roundToInt()
                    )
                }
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                query = "",
                onQueryChange = {},
                onSearch = {},
                active = false,
                onActiveChange = {
                    navController.slideFromRight(R.id.marketSearchFragment)
                },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                placeholder = { Text(text = stringResource(R.string.Market_Search_Hint)) }
            ) {}

            NiaTabRow(selectedTabIndex = selectedTab.ordinal) {
                tabs.forEach { title ->
                    NiaTab(
                        selected = title == selectedTab,
                        onClick = { marketViewModel.onSelect(title) },
                        text = { Text(text = stringResource(id = title.titleResId)) },
                    )
                }
            }
        }
    }

    /*Column(
        modifier = Modifier
            .background(color = ComposeAppTheme.colors.tyler)
            .nestedScroll(nestedScrollConnection)
    ) {






    }*/

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DockedSearchBar(content: @Composable () -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        // Talkback focus order sorts based on x and y position before considering z-index. The
        // extra Box with semantics and fillMaxWidth is a workaround to get the search bar to focus
        // before the content.
        Box(
            Modifier
                .zIndex(1f)
                .fillMaxWidth()
        ) {
            DockedSearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                query = text,
                onQueryChange = { text = it },
                onSearch = { active = false },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text("Hinted search text") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = { Icon(Icons.Rounded.MoreVert, contentDescription = null) },
            ) {
                /*LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(4) { idx ->
                        val resultText = "Suggestion $idx"
                        ListItem(
                            modifier = Modifier.clickable {
                                text = resultText
                                active = false
                            },
                            headlineContent = {
                                Text(
                                    text = resultText,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = "Additional info",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            leadingContent = { Icon(Icons.Rounded.Star, contentDescription = null) },
                        )
                    }
                }*/
            }
        }

        content.invoke()

        /*// Want to display value by default ? Let's do it here
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val list = List(100) { "Text $it" }
            items(count = list.size) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = list[it],
                    style = MaterialTheme.typography.titleMedium.copy(Color.White)
                )
            }
        }*/
    }
}
