package io.horizontalsystems.bankwallet.modules.market.overview

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.BuildConfig
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.AdType
import io.horizontalsystems.bankwallet.core.MaxTemplateNativeAdViewComposable
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Loading
import io.horizontalsystems.bankwallet.modules.market.category.MarketCategoryFragment
import io.horizontalsystems.bankwallet.modules.market.overview.ui.BoardsView
import io.horizontalsystems.bankwallet.modules.market.overview.ui.MetricChartsView
import io.horizontalsystems.bankwallet.modules.market.overview.ui.TopPlatformsBoardView
import io.horizontalsystems.bankwallet.modules.market.overview.ui.TopSectorsBoardView
import io.horizontalsystems.bankwallet.modules.market.platform.MarketPlatformFragment
import io.horizontalsystems.bankwallet.modules.market.topcoins.MarketTopCoinsFragment
import io.horizontalsystems.bankwallet.modules.market.topplatforms.TopPlatformsFragment
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.components.ListErrorView
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer

@Composable
fun MarketOverviewScreen(
    navController: NavController,
    viewModel: MarketOverviewViewModel
) {
    val isRefreshing by viewModel.isRefreshingLiveData.observeAsState(false)
    val viewState by viewModel.viewStateLiveData.observeAsState()
    val viewItem by viewModel.viewItem.observeAsState()

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val nativeAd by viewModel.adState
    LaunchedEffect(key1 = BuildConfig.HOME_MARKET_NATIVE, block = {
        viewModel.loadAds(context, BuildConfig.HOME_MARKET_NATIVE)
    })
    HSSwipeRefresh(
        refreshing = isRefreshing,
        onRefresh = {
            viewModel.refresh()
        }
    ) {
        Crossfade(viewState, label = "MarketOverviewScreen") { viewState ->
            when (viewState) {
                ViewState.Loading -> {
                    Loading()
                }
                is ViewState.Error -> {
                    ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                }
                ViewState.Success -> {
                    viewItem?.let { viewItem ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            Box(
                                modifier = Modifier.height(142.dp)
                            ) {
                                MetricChartsView(viewItem.marketMetrics, navController)
                            }
                            MaxTemplateNativeAdViewComposable(nativeAd, AdType.MEDIUM)
                            Spacer(modifier = Modifier.height(16.dp))
                            BoardsView(
                                boards = viewItem.boards,
                                navController = navController,
                                onClickSeeAll = { listType ->
                                    val (sortingField, topMarket, marketField) = viewModel.getTopCoinsParams(
                                        listType
                                    )
                                    val args = MarketTopCoinsFragment.prepareParams(
                                        sortingField,
                                        topMarket,
                                        marketField
                                    )

                                    navController.slideFromBottom(R.id.marketTopCoinsFragment, args)
                                },
                                onSelectTopMarket = { topMarket, listType ->
                                    viewModel.onSelectTopMarket(topMarket, listType)
                                }
                            )

                            TopPlatformsBoardView(
                                viewItem.topPlatformsBoard,
                                onSelectTimeDuration = { timeDuration ->
                                    viewModel.onSelectTopPlatformsTimeDuration(timeDuration)
                                },
                                onItemClick = {
                                    val args = MarketPlatformFragment.prepareParams(it)
                                    navController.slideFromRight(R.id.marketPlatformFragment, args)
                                },
                                onClickSeeAll = {
                                    val timeDuration = viewModel.topPlatformsTimeDuration
                                    val args = TopPlatformsFragment.prepareParams(timeDuration)

                                    navController.slideFromBottom(R.id.marketTopPlatformsFragment, args)
                                }
                            )

                            TopSectorsBoardView(
                                board = viewItem.topSectorsBoard
                            ) { coinCategory ->
                                navController.slideFromBottom(
                                    R.id.marketCategoryFragment,
                                    bundleOf(MarketCategoryFragment.categoryKey to coinCategory)
                                )
                            }

                            VSpacer(height = 32.dp)
                        }
                    }
                }
                null -> {}
            }
        }
    }
}
