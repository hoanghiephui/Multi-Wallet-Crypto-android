package io.horizontalsystems.bankwallet.modules.search

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
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
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchViewModel
import io.horizontalsystems.bankwallet.modules.settings.appstatus.AppStatusModule.BlockContent.Text
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchResults
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: MarketSearchViewModel,
    navController: NavController,
) {
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
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
                            }) {
                                Icon(Icons.Rounded.MoreVert, contentDescription = null)

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
                        }
                    ) { favorite, coinUid ->
                        searchViewModel.onFavoriteClick(favorite, coinUid)
                    }
                }
            }
        }
    }
}