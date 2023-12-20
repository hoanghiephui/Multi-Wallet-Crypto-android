package io.horizontalsystems.bankwallet.modules.main

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.managers.RateAppManager
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.balance.ui.BalanceScreen
import io.horizontalsystems.bankwallet.modules.keystore.KeyStoreActivity
import io.horizontalsystems.bankwallet.modules.keystore.NoSystemLockWarning
import io.horizontalsystems.bankwallet.modules.launcher.LaunchModule
import io.horizontalsystems.bankwallet.modules.launcher.LaunchViewModel
import io.horizontalsystems.bankwallet.modules.main.MainModule.MainNavigation
import io.horizontalsystems.bankwallet.modules.manageaccount.dialogs.BackupRequiredDialog
import io.horizontalsystems.bankwallet.modules.market.MarketScreen
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchModule
import io.horizontalsystems.bankwallet.modules.market.search.MarketSearchViewModel
import io.horizontalsystems.bankwallet.modules.rateapp.RateApp
import io.horizontalsystems.bankwallet.modules.releasenotes.ReleaseNotesFragment
import io.horizontalsystems.bankwallet.modules.rooteddevice.RootedDeviceModule
import io.horizontalsystems.bankwallet.modules.rooteddevice.RootedDeviceScreen
import io.horizontalsystems.bankwallet.modules.rooteddevice.RootedDeviceViewModel
import io.horizontalsystems.bankwallet.modules.settings.main.SettingsScreen
import io.horizontalsystems.bankwallet.modules.tor.TorStatusView
import io.horizontalsystems.bankwallet.modules.transactions.TransactionsModule
import io.horizontalsystems.bankwallet.modules.transactions.TransactionsScreen
import io.horizontalsystems.bankwallet.modules.transactions.TransactionsViewModel
import io.horizontalsystems.bankwallet.modules.walletconnect.WCAccountTypeNotSupportedDialog
import io.horizontalsystems.bankwallet.modules.walletconnect.version2.WC2Manager.SupportState
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.DisposableLifecycleCallbacks
import io.horizontalsystems.bankwallet.ui.compose.NiaNavigationBar
import io.horizontalsystems.bankwallet.ui.compose.NiaNavigationBarItem
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground
import io.horizontalsystems.bankwallet.ui.extensions.HeaderUpdate
import io.horizontalsystems.bankwallet.ui.extensions.WalletSwitchBottomSheet
import io.horizontalsystems.bankwallet.ui.extensions.rememberLifecycleEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import se.warting.inappupdate.compose.rememberInAppUpdateState

class MainFragment : BaseComposeFragment() {

    private val transactionsViewModel by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }
    private val searchViewModel by viewModels<MarketSearchViewModel> { MarketSearchModule.Factory() }
    private var intentUri: Uri? = null

    @Composable
    override fun GetContent(navController: NavController) {
        NiaBackground {
            MainScreenWithRootedDeviceCheck(
                transactionsViewModel = transactionsViewModel,
                deepLink = intentUri,
                navController = navController,
                searchViewModel = searchViewModel
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentUri = activity?.intent?.data
        activity?.intent?.data = null //clear intent data

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().moveTaskToBack(true)
                }
            })
    }

}

@Composable
private fun MainScreenWithRootedDeviceCheck(
    transactionsViewModel: TransactionsViewModel,
    deepLink: Uri?,
    navController: NavController,
    rootedDeviceViewModel: RootedDeviceViewModel = viewModel(factory = RootedDeviceModule.Factory()),
    searchViewModel: MarketSearchViewModel
) {
    if (rootedDeviceViewModel.showRootedDeviceWarning) {
        RootedDeviceScreen { rootedDeviceViewModel.ignoreRootedDeviceWarning() }
    } else {
        MainScreen(
            transactionsViewModel,
            deepLink,
            navController,
            searchViewModel = searchViewModel
        )
    }
}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class
)
@Composable
private fun MainScreen(
    transactionsViewModel: TransactionsViewModel,
    deepLink: Uri?,
    fragmentNavController: NavController,
    viewModel: MainViewModel = viewModel(factory = MainModule.Factory(deepLink)),
    searchViewModel: MarketSearchViewModel,
) {
    val launchViewModel = viewModel<LaunchViewModel>(factory = LaunchModule.Factory())
    val uiState = viewModel.uiState
    val selectedPage = uiState.selectedTabIndex
    val pagerState = rememberPagerState(initialPage = selectedPage) { uiState.mainNavItems.size }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val lifecycleEvent = rememberLifecycleEvent()
    val updateState = rememberInAppUpdateState()
    val manager: ReviewManager = ReviewManagerFactory.create(context)
    val request = manager.requestReviewFlow()

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetBackgroundColor = ComposeAppTheme.colors.transparent,
        sheetContent = {
            HeaderUpdate(
                updateState, context, modalBottomSheetState, coroutineScope
            )
        },
    ) {
        Box(Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    Column {
                        if (uiState.torEnabled) {
                            TorStatusView()
                        }
                        NiaBottomBar(
                            destinations = uiState.mainNavItems,
                            onNavigateToDestination = {
                                viewModel.onSelect(it.mainNavItem)
                            }
                        )
                    }
                }
            ) { padding ->
                BackHandler(enabled = modalBottomSheetState.isVisible) {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal,
                            ),
                        )
                ) {
                    LaunchedEffect(key1 = selectedPage, block = {
                        pagerState.scrollToPage(selectedPage)
                    })

                    HorizontalPager(
                        modifier = Modifier.weight(1f),
                        state = pagerState,
                        userScrollEnabled = false,
                        verticalAlignment = Alignment.Top
                    ) { page ->
                        when (uiState.mainNavItems[page].mainNavItem) {
                            MainNavigation.Market -> MarketScreen(
                                fragmentNavController,
                                searchViewModel
                            )

                            MainNavigation.Balance -> {
                                if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
                                    when (launchViewModel.getPage()) {
                                        LaunchViewModel.Page.Unlock -> {
                                        }

                                        LaunchViewModel.Page.NoSystemLock -> {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize(),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                NoSystemLockWarning()
                                            }
                                        }

                                        LaunchViewModel.Page.KeyInvalidated -> {
                                            KeyStoreActivity.startForInvalidKey(context)
                                        }

                                        LaunchViewModel.Page.UserAuthentication -> {
                                            KeyStoreActivity.startForUserAuthentication(context)
                                        }

                                        LaunchViewModel.Page.Main -> {
                                            BalanceScreen(fragmentNavController)
                                        }

                                        else -> Unit

                                    }
                                }
                            }

                            MainNavigation.Transactions -> TransactionsScreen(
                                fragmentNavController,
                                transactionsViewModel
                            )

                            MainNavigation.Settings -> SettingsScreen(fragmentNavController)
                        }
                    }
                }
            }
            HideContentBox(uiState.contentHidden)
        }
    }

    if (uiState.showWhatsNew) {
        LaunchedEffect(Unit) {
            fragmentNavController.slideFromBottom(
                R.id.releaseNotesFragment,
                bundleOf(ReleaseNotesFragment.showAsClosablePopupKey to true)
            )
            viewModel.whatsNewShown()
        }
    }
    try {
        request.addOnCompleteListener { task ->
            if (task.isSuccessful && uiState.showRateAppDialog) {
                val reviewInfo = task.result
                reviewInfo.let {
                    val flow = manager.launchReviewFlow(context as Activity, it)
                    flow.addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            //log
                        } else {
                            //log
                        }
                    }
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    if (uiState.wcSupportState != null) {
        when (val wcSupportState = uiState.wcSupportState) {
            SupportState.NotSupportedDueToNoActiveAccount -> {
                fragmentNavController.slideFromBottom(R.id.wcErrorNoAccountFragment)
            }

            is SupportState.NotSupportedDueToNonBackedUpAccount -> {
                val text = stringResource(R.string.WalletConnect_Error_NeedBackup)
                fragmentNavController.slideFromBottom(
                    R.id.backupRequiredDialog,
                    BackupRequiredDialog.prepareParams(wcSupportState.account, text)
                )
            }

            is SupportState.NotSupported -> {
                fragmentNavController.slideFromBottom(
                    R.id.wcAccountTypeNotSupportedDialog,
                    WCAccountTypeNotSupportedDialog.prepareParams(wcSupportState.accountTypeDescription)
                )
            }

            else -> Unit
        }
        viewModel.wcSupportStateHandled()
    }

    uiState.deeplinkPage?.let { deepLinkPage ->
        LaunchedEffect(Unit) {
            delay(500)
            fragmentNavController.slideFromRight(
                deepLinkPage.navigationId,
                deepLinkPage.bundle
            )
            viewModel.deeplinkPageHandled()
        }
    }

    DisposableLifecycleCallbacks(
        onResume = viewModel::onResume,
    )
}

@Composable
private fun HideContentBox(contentHidden: Boolean) {
    val backgroundModifier = if (contentHidden) {
        Modifier.background(ComposeAppTheme.colors.tyler)
    } else {
        Modifier
    }
    Box(
        Modifier
            .fillMaxSize()
            .then(backgroundModifier)
    )
}

@Composable
private fun BadgedIcon(
    badge: MainModule.BadgeType?,
    icon: @Composable BoxScope.() -> Unit,
) {
    when (badge) {
        is MainModule.BadgeType.BadgeNumber ->
            BadgedBox(
                badge = {
                    Badge(
                        contentColor = ComposeAppTheme.colors.lucian
                    ) {
                        Text(
                            text = badge.number.toString(),
                            style = ComposeAppTheme.typography.micro,
                            color = ComposeAppTheme.colors.white,
                        )
                    }
                },
                content = icon
            )

        MainModule.BadgeType.BadgeDot ->
            BadgedBox(
                badge = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                ComposeAppTheme.colors.lucian,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) { }
                },
                content = icon
            )

        else -> {
            Box {
                icon()
            }
        }
    }
}

@Composable
private fun NiaBottomBar(
    destinations: List<MainModule.NavigationViewItem>,
    onNavigateToDestination: (MainModule.NavigationViewItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    NiaNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            val selected = destination.selected
            NiaNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        painter = painterResource(destination.mainNavItem.iconRes),
                        contentDescription = stringResource(destination.mainNavItem.titleRes)
                    )
                },
                selectedIcon = {
                    Icon(
                        painter = painterResource(destination.mainNavItem.iconRes),
                        contentDescription = stringResource(destination.mainNavItem.titleRes)
                    )
                },
                label = { Text(stringResource(destination.mainNavItem.titleRes)) },
                modifier = Modifier,
                enabled = destination.enabled
            )
        }
    }
}
