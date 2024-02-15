package io.horizontalsystems.bankwallet.modules.coin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.android.billing.UserDataRepository
import com.applovin.mediation.ads.MaxRewardedAd
import com.wallet.blockchain.bitcoin.BuildConfig
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.modules.billing.showBillingPlusDialog
import io.horizontalsystems.bankwallet.modules.coin.analytics.CoinAnalyticsScreen
import io.horizontalsystems.bankwallet.modules.coin.coinmarkets.CoinMarketsScreen
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.CoinOverviewScreen
import io.horizontalsystems.bankwallet.ui.AdMaxRewardedLoader
import io.horizontalsystems.bankwallet.ui.AdRewardedCallback
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.*
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.findActivity

class CoinFragment : BaseComposeFragment(), AdRewardedCallback {
    private val adMaxRewardedLoader = AdMaxRewardedLoader(this)
    private var viewModel: CoinViewModel? = null
    @Composable
    override fun GetContent(navController: NavController) {
        val coinUid = requireArguments().getString(COIN_UID_KEY, "")
        val apiTag = requireArguments().getString(API_TAG_KEY, "")
        viewModel = coinViewModel(coinUid, userDataRepository)
        CoinScreen(
            coinUid,
            apiTag,
            viewModel,
            navController,
            childFragmentManager
        ) {
            adMaxRewardedLoader.createRewardedAd(requireActivity(), BuildConfig.COIN_REWARD)
        }
    }

    override val logScreen: String
        get() = "CoinFragment"

    private fun coinViewModel(coinUid: String,
                              userDataRepository: UserDataRepository
    ): CoinViewModel? = try {
        val viewModel by navGraphViewModels<CoinViewModel>(R.id.coinFragment) {
            CoinModule.Factory(coinUid, userDataRepository)
        }
        viewModel
    } catch (e: Exception) {
        null
    }

    override fun onLoaded(rewardedAd: MaxRewardedAd) {
        if (rewardedAd.isReady) {
            rewardedAd.showAd()
        }
    }

    override fun onAdRewardLoadFail() {}

    override fun onUserRewarded(amount: Int) {
        viewModel?.onFavoriteClick()
    }

    override fun onShowFail() {}

    companion object {
        private const val COIN_UID_KEY = "coin_uid_key"
        private const val API_TAG_KEY = "api_tag_key"

        fun prepareParams(coinUid: String, apiTag: String) = bundleOf(COIN_UID_KEY to coinUid, API_TAG_KEY to apiTag)
    }
}

@Composable
fun CoinScreen(
    coinUid: String,
    apiTag: String,
    coinViewModel: CoinViewModel?,
    navController: NavController,
    fragmentManager: FragmentManager,
    openAds: () -> Unit
) {
    if (coinViewModel != null) {
        CoinTabs(apiTag, coinViewModel, navController, fragmentManager, openAds)
    } else {
        CoinNotFound(coinUid, navController)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CoinTabs(
    apiTag: String,
    viewModel: CoinViewModel,
    navController: NavController,
    fragmentManager: FragmentManager,
    openAds: () -> Unit
) {
    val tabs = viewModel.tabs
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val isPlusMode by viewModel.screenState.collectAsStateWithLifecycle()
    var openAlertDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
        AppBar(
            title = viewModel.fullCoin.coin.code,
            navigationIcon = {
                HsBackButton(onClick = { navController.popBackStack() })
            },
            menuItems = buildList {
                if (viewModel.isWatchlistEnabled) {
                    if (viewModel.isFavorite) {
                        add(
                            MenuItem(
                                title = TranslatableString.ResString(R.string.CoinPage_Unfavorite),
                                icon = R.drawable.ic_filled_star_24,
                                tint = MaterialTheme.colorScheme.onSurface,
                                onClick = { viewModel.onUnfavoriteClick() }
                            )
                        )
                    } else {
                        add(
                            MenuItem(
                                title = TranslatableString.ResString(R.string.CoinPage_Favorite),
                                icon = R.drawable.ic_star_24,
                                onClick = {
                                    if (isPlusMode) {
                                        viewModel.onFavoriteClick()
                                    } else {
                                        openAlertDialog = true
                                    }
                                }
                            )
                        )
                    }
                }
            }
        )

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
                        apiTag = apiTag,
                        fullCoin = viewModel.fullCoin,
                        navController = navController
                    )
                }

                CoinModule.Tab.Market -> {
                    CoinMarketsScreen(fullCoin = viewModel.fullCoin)
                }

                CoinModule.Tab.Details -> {
                    CoinAnalyticsScreen(
                        apiTag = apiTag,
                        fullCoin = viewModel.fullCoin,
                        navController = navController,
                        fragmentManager = fragmentManager
                    )
                }
//                CoinModule.Tab.Tweets -> {
//                    CoinTweetsScreen(fullCoin = viewModel.fullCoin)
//                }
            }
        }

        viewModel.successMessage?.let {
            HudHelper.showSuccessMessage(view, it)

            viewModel.onSuccessMessageShown()
        }
        if (openAlertDialog) {
            AlertDialog(
                title = {
                    body_jacob(text = stringResource(id = R.string.billing_plus_title))
                },
                text = {
                    Text(text = "To use the feature, you must subscribe to Wallet+ or watch ads in exchange for rewards.")
                },
                onDismissRequest = { openAlertDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        openAlertDialog = false
                        openAds.invoke()
                    }) {
                        Text(text = "View Ads")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        openAlertDialog = false
                        context.findActivity().showBillingPlusDialog()
                    }) {
                        Text(text = stringResource(id = R.string.billing_plus_title))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinNotFound(coinUid: String, navController: NavController) {
    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = coinUid,
            navigationIcon = {
                HsBackButton(onClick = { navController.popBackStack() })
            }
        )

        ListEmptyView(
            text = stringResource(R.string.CoinPage_CoinNotFound, coinUid),
            icon = R.drawable.ic_not_available
        )

    }
}
