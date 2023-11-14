package io.horizontalsystems.bankwallet.modules.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
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
import io.horizontalsystems.bankwallet.ui.extensions.WalletSwitchBottomSheet
import io.horizontalsystems.bankwallet.ui.extensions.rememberLifecycleEvent
import io.horizontalsystems.core.findNavController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainFragment : BaseComposeFragment() {

    private val transactionsViewModel by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }
    private val searchViewModel by viewModels<MarketSearchViewModel> { MarketSearchModule.Factory() }

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            NiaBackground {
                MainScreenWithRootedDeviceCheck(
                    transactionsViewModel = transactionsViewModel,
                    deepLink = activity?.intent?.data?.toString(),
                    navController = findNavController(),
                    clearActivityData = { activity?.intent?.data = null },
                    searchViewModel = searchViewModel
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    deepLink: String?,
    navController: NavController,
    clearActivityData: () -> Unit,
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
            clearActivityData,
            searchViewModel = searchViewModel
        )
    }
}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun MainScreen(
    transactionsViewModel: TransactionsViewModel,
    deepLink: String?,
    fragmentNavController: NavController,
    clearActivityData: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = MainModule.Factory(deepLink)),
    searchViewModel: MarketSearchViewModel,
) {
    val launchViewModel = viewModel<LaunchViewModel>(factory = LaunchModule.Factory())
    val uiState = viewModel.uiState
    val selectedPage = uiState.selectedPageIndex
    val pagerState = rememberPagerState(initialPage = selectedPage) { uiState.mainNavItems.size }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val lifecycleEvent = rememberLifecycleEvent()
    val bottomBarHeight = 60.dp
    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
    val bottomBarOffsetHeightPx = remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                val delta = available.y
                val newOffset = bottomBarOffsetHeightPx.floatValue + delta
                bottomBarOffsetHeightPx.floatValue = newOffset.coerceIn(-bottomBarHeightPx, 0f)

                return Offset.Zero
            }
        }
    }
    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetBackgroundColor = ComposeAppTheme.colors.transparent,
        sheetContent = {
            WalletSwitchBottomSheet(
                wallets = viewModel.wallets,
                watchingAddresses = viewModel.watchWallets,
                selectedAccount = uiState.activeWallet,
                onSelectListener = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                        viewModel.onSelect(it)
                    }
                },
                onCancelClick = {
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }
            )
        },
    ) {
        Box(Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.nestedScroll(nestedScrollConnection),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    AnimatedVisibility(
                        visible = bottomBarOffsetHeightPx.floatValue.roundToInt() >= 0,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
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
                            /*HsBottomNavigation(
                                backgroundColor = ComposeAppTheme.colors.tyler,
                                elevation = 10.dp
                            ) {
                                uiState.mainNavItems.forEach { item ->
                                    HsBottomNavigationItem(
                                        icon = {
                                            BadgedIcon(item.badge) {
                                                Icon(
                                                    painter = painterResource(item.mainNavItem.iconRes),
                                                    contentDescription = stringResource(item.mainNavItem.titleRes)
                                                )
                                            }
                                        },
                                        selected = item.selected,
                                        enabled = item.enabled,
                                        selectedContentColor = ComposeAppTheme.colors.jacob,
                                        unselectedContentColor = if (item.enabled) ComposeAppTheme.colors.grey else ComposeAppTheme.colors.grey50,
                                        onClick = { viewModel.onSelect(item.mainNavItem) },
                                        onLongClick = {
                                            if (item.mainNavItem == MainNavigation.Balance) {
                                                coroutineScope.launch {
                                                    modalBottomSheetState.show()
                                                }
                                            }
                                        }
                                    )
                                }
                            }*/
                        }
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

    if (uiState.showRateAppDialog) {
        RateApp(
            onRateClick = {
                RateAppManager.openPlayMarket(context)
                viewModel.closeRateDialog()
            },
            onCancelClick = { viewModel.closeRateDialog() }
        )
    }

    if (uiState.wcSupportState != null) {
        when (val wcSupportState = uiState.wcSupportState) {
            SupportState.Supported -> {
                fragmentNavController.slideFromRight(R.id.wallet_connect_graph)
            }

            SupportState.NotSupportedDueToNoActiveAccount -> {
                clearActivityData.invoke()
                fragmentNavController.slideFromBottom(R.id.wcErrorNoAccountFragment)
            }

            is SupportState.NotSupportedDueToNonBackedUpAccount -> {
                clearActivityData.invoke()
                val text = stringResource(R.string.WalletConnect_Error_NeedBackup)
                fragmentNavController.slideFromBottom(
                    R.id.backupRequiredDialog,
                    BackupRequiredDialog.prepareParams(wcSupportState.account, text)
                )
            }

            is SupportState.NotSupported -> {
                clearActivityData.invoke()
                fragmentNavController.slideFromBottom(
                    R.id.wcAccountTypeNotSupportedDialog,
                    WCAccountTypeNotSupportedDialog.prepareParams(wcSupportState.accountTypeDescription)
                )
            }

            else -> Unit
        }
        viewModel.wcSupportStateHandled()
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
                    androidx.compose.material3.Icon(
                        painter = painterResource(destination.mainNavItem.iconRes),
                        contentDescription = stringResource(destination.mainNavItem.titleRes)
                    )
                },
                selectedIcon = {
                    androidx.compose.material3.Icon(
                        painter = painterResource(destination.mainNavItem.iconRes),
                        contentDescription = stringResource(destination.mainNavItem.titleRes)
                    )
                },
                label = { androidx.compose.material3.Text(stringResource(destination.mainNavItem.titleRes)) },
                modifier = Modifier,
                enabled = destination.enabled
            )
        }
    }
}
