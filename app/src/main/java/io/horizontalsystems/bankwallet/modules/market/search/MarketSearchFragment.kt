package io.horizontalsystems.bankwallet.modules.market.search

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.iconPlaceholder
import io.horizontalsystems.bankwallet.core.imageUrl
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Loading
import io.horizontalsystems.bankwallet.modules.market.MarketDataValue
import io.horizontalsystems.bankwallet.modules.market.TimeDuration
import io.horizontalsystems.bankwallet.modules.market.category.MarketCategoryFragment
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchModule.CoinItem
import io.horizontalsystems.bankwallet.ui.compose.ColoredTextStyle
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.Select
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonSecondaryCircle
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonSecondaryToggle
import io.horizontalsystems.bankwallet.ui.compose.components.CategoryCard
import io.horizontalsystems.bankwallet.ui.compose.components.CoinImage
import io.horizontalsystems.bankwallet.ui.compose.components.HeaderSorting
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.HsIconButton
import io.horizontalsystems.bankwallet.ui.compose.components.ListEmptyView
import io.horizontalsystems.bankwallet.ui.compose.components.ListErrorView
import io.horizontalsystems.bankwallet.ui.compose.components.MarketCoinFirstRow
import io.horizontalsystems.bankwallet.ui.compose.components.MarketCoinSecondRow
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground
import io.horizontalsystems.bankwallet.ui.compose.components.SectionItemBorderedRowUniversalClear
import io.horizontalsystems.bankwallet.ui.compose.components.SnackbarError
import io.horizontalsystems.bankwallet.ui.compose.components.body_grey50
import io.horizontalsystems.bankwallet.ui.compose.components.headline2_jacob
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.marketkit.models.Coin

class MarketSearchFragment : BaseComposeFragment() {

    private val viewModel by viewModels<MarketSearchViewModel> { MarketSearchModule.Factory() }

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            MarketSearchScreen(
                viewModel,
                findNavController(),
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketSearchScreen(
    viewModel: MarketSearchViewModel,
    navController: NavController,
) {

    val viewState = viewModel.viewState
    val errorMessage = viewModel.errorMessage

    NiaBackground {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            AppBar(
                title = stringResource(R.string.Market_Overview_TopSectors),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Market_Filters),
                        enabled = true,
                        onClick = {
                            navController.slideFromRight(R.id.marketAdvancedSearchFragment)
                        },
                        icon = R.drawable.baseline_filter_list_24
                    )
                )
            )
            Crossfade(viewState, label = "SearchView") { viewState ->
                when (viewState) {
                    ViewState.Loading -> {
                        Loading()
                    }
                    is ViewState.Error -> {
                        ListErrorView(stringResource(R.string.SyncError), viewModel::refresh)
                    }
                    ViewState.Success -> {
                        when (val itemsData = viewModel.itemsData) {
                            is MarketSearchModule.Data.DiscoveryItems -> {
                                CardsGrid(
                                    viewItems = itemsData.discoveryItems,
                                    timePeriodSelect = viewModel.timePeriodMenu,
                                    sortDescending = viewModel.sortDescending,
                                    onToggleSortType = {
                                        viewModel.toggleSortType()
                                    },
                                    onCategoryClick = { viewItemType ->
                                        when (viewItemType) {
                                            MarketSearchModule.DiscoveryItem.TopCoins -> {
                                                navController.slideFromBottom(
                                                    R.id.marketTopCoinsFragment
                                                )
                                            }
                                            is MarketSearchModule.DiscoveryItem.Category -> {
                                                navController.slideFromBottom(
                                                    R.id.marketCategoryFragment,
                                                    bundleOf(MarketCategoryFragment.categoryKey to viewItemType.coinCategory)
                                                )
                                            }
                                        }
                                    }
                                ) { viewModel.toggleTimePeriod(it) }
                            }
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
                                            val arguments = CoinFragment.prepareParams(coin.uid)
                                            navController.slideFromRight(
                                                R.id.coinFragment,
                                                arguments
                                            )
                                        }
                                    ) { favorited, coinUid ->
                                        viewModel.onFavoriteClick(favorited, coinUid)
                                    }
                                }
                            }
                            null -> {}
                        }
                    }
                }
            }
        }

        errorMessage?.let {
            SnackbarError(it.getString())
            viewModel.errorShown()
        }
    }
}

@Composable
fun MarketSearchResults(
    coinResult: List<CoinItem>,
    onCoinClick: (Coin) -> Unit,
    onFavoriteClick: (Boolean, String) -> Unit
) {
    LazyColumn {
        item {
            Divider(
                thickness = 1.dp,
                color = ComposeAppTheme.colors.steel10,
            )
        }
        items(coinResult) { coinViewItem ->
            MarketCoin(
                coinViewItem.fullCoin.coin.code,
                coinViewItem.fullCoin.coin.name,
                coinViewItem.fullCoin.coin.imageUrl,
                coinViewItem.fullCoin.iconPlaceholder,
                favorited = coinViewItem.favourited,
                onClick = { onCoinClick(coinViewItem.fullCoin.coin) },
                onFavoriteClick = {
                    onFavoriteClick(
                        coinViewItem.favourited,
                        coinViewItem.fullCoin.coin.uid
                    )
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SearchView(
    onSearchTextChange: (String) -> Unit,
    onRightTextButtonClick: () -> Unit,
    leftIcon: Int,
    onBackButtonClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var searchText by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onBackButtonClick.invoke()
            }
        ) {
            Icon(
                painter = painterResource(id = leftIcon),
                contentDescription = "back icon",
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .size(24.dp),
                tint = ComposeAppTheme.colors.jacob
            )
        }
        BasicTextField(
            value = searchText,
            onValueChange = { value ->
                searchText = value
                onSearchTextChange(value)
            },
            modifier = Modifier
                .weight(1f),
            singleLine = true,
            textStyle = ColoredTextStyle(
                color = ComposeAppTheme.colors.leah,
                textStyle = ComposeAppTheme.typography.body
            ),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    body_grey50(stringResource(R.string.Market_Search_Hint))
                }
                innerTextField()
            },
            cursorBrush = SolidColor(ComposeAppTheme.colors.jacob),
        )
        Box(
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onRightTextButtonClick.invoke()
            }
        ) {
            headline2_jacob(
                text = stringResource(R.string.Market_Filters),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

}

@Composable
fun CardsGrid(
    viewItems: List<MarketSearchModule.DiscoveryItem>,
    timePeriodSelect: Select<TimeDuration>,
    sortDescending: Boolean,
    onToggleSortType: () -> Unit,
    onCategoryClick: (MarketSearchModule.DiscoveryItem) -> Unit,
    onTimePeriodMenuToggle: (TimeDuration) -> Unit
) {

    var timePeriodMenu by remember { mutableStateOf(timePeriodSelect) }

    LazyColumn {
        item {
            HeaderSorting(borderTop = true) {
                ButtonSecondaryCircle(
                    modifier = Modifier
                        .padding(start = 16.dp),
                    icon = if (sortDescending) R.drawable.ic_arrow_down_20 else R.drawable.ic_arrow_up_20,
                    onClick = { onToggleSortType() }
                )
                Spacer(Modifier.weight(1f))
                ButtonSecondaryToggle(
                    modifier = Modifier.padding(end = 16.dp),
                    select = timePeriodMenu,
                    onSelect = {
                        onTimePeriodMenuToggle.invoke(it)
                        timePeriodMenu = Select(it, timePeriodSelect.options)
                    }
                )
            }
            Spacer(Modifier.height(4.dp))
        }
        // Turning the list in a list of lists of two elements each
        items(viewItems.windowed(2, 2, true)) { chunk ->
            Row(modifier = Modifier.padding(horizontal = 10.dp)) {
                CategoryCard(chunk[0]) { onCategoryClick(chunk[0]) }
                if (chunk.size > 1) {
                    CategoryCard(chunk[1]) { onCategoryClick(chunk[1]) }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MarketCoin(
    coinCode: String,
    coinName: String,
    coinIconUrl: String,
    coinIconPlaceholder: Int,
    favorited: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    coinRate: String? = null,
    marketDataValue: MarketDataValue? = null,
) {

    SectionItemBorderedRowUniversalClear(
        borderBottom = true,
        onClick = onClick
    ) {
        CoinImage(
            iconUrl = coinIconUrl,
            placeholder = coinIconPlaceholder,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(32.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            MarketCoinFirstRow(coinCode, coinRate)
            Spacer(modifier = Modifier.height(3.dp))
            MarketCoinSecondRow(coinName, marketDataValue, null)
        }

        HsIconButton(onClick = onFavoriteClick) {
            Icon(
                painter = painterResource(if (favorited) R.drawable.ic_star_filled_20 else R.drawable.ic_star_20),
                contentDescription = "coin icon",
                tint = if (favorited) ComposeAppTheme.colors.jacob else ComposeAppTheme.colors.grey
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MarketCoinPreview() {
    val coin = Coin("ether", "Ethereum", "ETH")
    ComposeAppTheme {
        MarketCoin(
            coin.code,
            coin.name,
            coin.imageUrl,
            R.drawable.coin_placeholder,
            false,
            {},
            {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchViewPreview() {
    ComposeAppTheme {
        SearchView(
            onSearchTextChange = { },
            onRightTextButtonClick = { },
            leftIcon = R.drawable.ic_back,
            onBackButtonClick = { }
        )
    }
}
