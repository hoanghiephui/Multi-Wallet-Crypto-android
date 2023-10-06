package io.horizontalsystems.bankwallet.modules.coin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import coin.chain.crypto.core.designsystem.component.TopAppBar
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.modules.coin.analytics.CoinAnalyticsScreen
import io.horizontalsystems.bankwallet.modules.coin.coinmarkets.CoinMarketsScreen
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.CoinOverviewScreen
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.ListEmptyView
import io.horizontalsystems.bankwallet.ui.compose.components.TabItem
import io.horizontalsystems.bankwallet.ui.compose.components.Tabs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoinFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val uid = try {
            activity?.intent?.data?.getQueryParameter("uid")
        } catch (e: UnsupportedOperationException) {
            null
        }

        val coinUid = requireArguments().getString(COIN_UID_KEY, uid ?: "")
        if (uid != null) {
            activity?.intent?.data = null
        }

        /*CoinScreen(
            coinUid,
            coinViewModel(coinUid),
            findNavController(),
            childFragmentManager
        )*/
    }

    private fun coinViewModel(coinUid: String): CoinViewModel? = try {
        val viewModel by navGraphViewModels<CoinViewModel>(R.id.coinFragment) {
            CoinModule.Factory(coinUid)
        }
        viewModel
    } catch (e: Exception) {
        null
    }

    companion object {
        const val COIN_UID_KEY = "coin_uid_key"

        fun prepareParams(coinUid: String) = bundleOf(COIN_UID_KEY to coinUid)
    }
}

@Composable
fun CoinScreen(
    coinUid: String,
    coinViewModel: CoinViewModel?,
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    ComposeAppTheme {
        if (coinViewModel != null) {
            CoinTabs(coinViewModel, navController, onShowSnackbar)
        } else {
            CoinNotFound(coinUid, navController)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun CoinTabs(
    viewModel: CoinViewModel,
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val tabs = viewModel.tabs
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    Scaffold(
        contentColor = Color.Transparent,
        containerColor = Color.Transparent,
        topBar = {
            val actionIcon = if (viewModel.isWatchlistEnabled) {
                if (viewModel.isFavorite) {
                    Icons.Rounded.Favorite
                } else {
                    Icons.Rounded.FavoriteBorder
                }
            } else {
                null
            }
            TopAppBar(
                titleRes = viewModel.fullCoin.coin.code,
                navigationIcon = Icons.Rounded.ArrowBack,
                actionIconContentDescription = "ArrowBack",
                onNavigationClick = { navController.popBackStack() },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                actionIcon = actionIcon,
                onActionClick = {
                    if (viewModel.isWatchlistEnabled) {
                        if (viewModel.isFavorite) {
                            viewModel.onUnfavoriteClick()
                        } else {
                            viewModel.onFavoriteClick()
                        }
                    }
                },
                modifier = Modifier
            )
        },

    ) {
        Column(modifier = Modifier.padding(it)) {
            val selectedTab = tabs[pagerState.currentPage]
            val tabItems = tabs.map {
                TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
            }
            Tabs(tabItems, onClick = { tab ->
                coroutineScope.launch {
                    pagerState.scrollToPage(tab.ordinal)

                    if (tab == CoinModule.Tab.Details && viewModel.shouldShowSubscriptionInfo()) {
                        viewModel.subscriptionInfoShown()

                        delay(1000)
                        navController.slideFromBottom(R.id.subscriptionInfoFragment)
                    }
                }
            })

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (tabs[page]) {
                    CoinModule.Tab.Overview -> {
                        CoinOverviewScreen(
                            fullCoin = viewModel.fullCoin,
                            navController = navController,
                            onShowSnackbar = onShowSnackbar
                        )
                    }

                    CoinModule.Tab.Market -> {
                        CoinMarketsScreen(fullCoin = viewModel.fullCoin)
                    }

                    CoinModule.Tab.Details -> {
                        CoinAnalyticsScreen(
                            fullCoin = viewModel.fullCoin,
                            navController = navController,
                            //fragmentManager = fragmentManager
                        )
                    }
//                CoinModule.Tab.Tweets -> {
//                    CoinTweetsScreen(fullCoin = viewModel.fullCoin)
//                }
                }
            }

            viewModel.successMessage?.let {
                val message = stringResource(id = it)
                coroutineScope.launch {
                    onShowSnackbar.invoke(message, null)
                }
                viewModel.onSuccessMessageShown()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinNotFound(coinUid: String, navController: NavController) {
    Column {
        TopAppBar(
            titleRes = coinUid,
            navigationIcon = Icons.Rounded.ArrowBack,
            navigationIconContentDescription = "ArrowBack",
            onNavigationClick = { navController.popBackStack() },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
            )
        )

        ListEmptyView(
            text = stringResource(R.string.CoinPage_CoinNotFound, coinUid),
            icon = R.drawable.ic_not_available
        )

    }
}
