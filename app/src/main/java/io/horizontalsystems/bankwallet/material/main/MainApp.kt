package io.horizontalsystems.bankwallet.material.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import coin.chain.crypto.core.data.util.NetworkMonitor
import coin.chain.crypto.core.designsystem.component.NiaBackground
import coin.chain.crypto.core.designsystem.component.NiaGradientBackground
import coin.chain.crypto.core.designsystem.component.NiaNavigationBar
import coin.chain.crypto.core.designsystem.component.NiaNavigationBarItem
import coin.chain.crypto.core.designsystem.component.NiaNavigationRail
import coin.chain.crypto.core.designsystem.component.NiaNavigationRailItem
import coin.chain.crypto.core.designsystem.component.NiaTopAppBar
import coin.chain.crypto.core.designsystem.icon.NiaIcons
import coin.chain.crypto.core.designsystem.theme.GradientColors
import coin.chain.crypto.core.designsystem.theme.LocalGradientColors
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.material.navigation.NiaNavHost
import io.horizontalsystems.bankwallet.material.navigation.TopLevelDestination
import io.horizontalsystems.bankwallet.modules.main.HideContentBox
import io.horizontalsystems.bankwallet.modules.main.MainModule
import io.horizontalsystems.bankwallet.modules.main.MainViewModel
import io.horizontalsystems.bankwallet.modules.rooteddevice.RootedDeviceModule
import io.horizontalsystems.bankwallet.modules.rooteddevice.RootedDeviceScreen
import io.horizontalsystems.bankwallet.modules.rooteddevice.RootedDeviceViewModel
import io.horizontalsystems.bankwallet.modules.tor.TorStatusView
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.extensions.WalletSwitchBottomSheet
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class,
)
@Composable
fun MainApp(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    appState: NiaAppState = rememberNiaAppState(
        networkMonitor = networkMonitor,
        windowSizeClass = windowSizeClass,
    ),
    deepLink: String?,
    rootedDeviceViewModel: RootedDeviceViewModel = viewModel(factory = RootedDeviceModule.Factory()),
    viewModel: MainViewModel = viewModel(factory = MainModule.Factory(deepLink)),
) {
    if (rootedDeviceViewModel.showRootedDeviceWarning) {
        RootedDeviceScreen { rootedDeviceViewModel.ignoreRootedDeviceWarning() }
    } else {
        val shouldShowGradientBackground =
            appState.currentTopLevelDestination == TopLevelDestination.MARKETS
        var showSettingsDialog by rememberSaveable {
            mutableStateOf(false)
        }

        val uiState = viewModel.uiState
        val coroutineScope = rememberCoroutineScope()
        val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

        NiaBackground {
            NiaGradientBackground(
                gradientColors = if (shouldShowGradientBackground) {
                    LocalGradientColors.current
                } else {
                    GradientColors()
                },
            ) {
                val snackbarHostState = remember { SnackbarHostState() }

                val isOffline by appState.isOffline.collectAsStateWithLifecycle()

                // If user is not connected to the internet show a snack bar to inform them.
                val notConnectedMessage = stringResource(R.string.WalletConnect_Reconnect_Hint)
                LaunchedEffect(isOffline) {
                    if (isOffline) {
                        snackbarHostState.showSnackbar(
                            message = notConnectedMessage,
                            duration = SnackbarDuration.Indefinite,
                        )
                    }
                }
                val destination = appState.currentTopLevelDestination

                ModalBottomSheetLayout(
                    sheetState = modalBottomSheetState,
                    sheetBackgroundColor = Color.Transparent,
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
                            modifier = Modifier.semantics {
                                testTagsAsResourceId = true
                            },
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            contentWindowInsets = WindowInsets(0, 0, 0, 0),
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            bottomBar = {
                                Column {
                                    if (uiState.torEnabled) {
                                        TorStatusView()
                                    }
                                    if (appState.shouldShowBottomBar) {
                                        NiaBottomBar(
                                            uiState.mainNavItems,
                                            destinations = appState.topLevelDestinations,
                                            destinationsWithUnreadResources = setOf(),
                                            onNavigateToDestination = appState::navigateToTopLevelDestination,
                                            currentDestination = appState.currentDestination,
                                            modifier = Modifier.testTag("NiaBottomBar"),
                                            onLongClick = {
                                                coroutineScope.launch {
                                                    modalBottomSheetState.show()
                                                }
                                                if (destination == TopLevelDestination.BALANCE) {

                                                }
                                            }
                                        )
                                    }
                                }
                            },
                        ) { padding ->
                            BackHandler(enabled = modalBottomSheetState.isVisible) {
                                coroutineScope.launch {
                                    modalBottomSheetState.hide()
                                }
                            }
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .padding(padding)
                                    .consumeWindowInsets(padding)
                                    .windowInsetsPadding(
                                        WindowInsets.safeDrawing.only(
                                            WindowInsetsSides.Horizontal,
                                        ),
                                    ),
                            ) {
                                if (appState.shouldShowNavRail) {
                                    NiaNavRail(
                                        destinations = appState.topLevelDestinations,
                                        destinationsWithUnreadResources = setOf(),
                                        onNavigateToDestination = appState::navigateToTopLevelDestination,
                                        currentDestination = appState.currentDestination,
                                        modifier = Modifier
                                            .testTag("NiaNavRail")
                                            .safeDrawingPadding(),
                                    )
                                }

                                Column(Modifier.fillMaxSize()) {
                                    // Show the top app bar on top level destinations.
                                    if (destination != null) {
                                        NiaTopAppBar(
                                            titleRes = destination.titleTextId,
                                            navigationIcon = NiaIcons.Search,
                                            navigationIconContentDescription = stringResource(
                                                id = R.string.Settings_Title,
                                            ),
                                            actionIcon = NiaIcons.Settings,
                                            actionIconContentDescription = stringResource(
                                                id = R.string.Settings_Title,
                                            ),
                                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                                containerColor = Color.Transparent,
                                            ),
                                            onActionClick = { showSettingsDialog = true },
                                            onNavigationClick = { appState.navigateToSearch() },
                                        )
                                    }

                                    NiaNavHost(appState = appState, onShowSnackbar = { message, action ->
                                        snackbarHostState.showSnackbar(
                                            message = message,
                                            actionLabel = action,
                                            duration = SnackbarDuration.Short,
                                        ) == SnackbarResult.ActionPerformed
                                    })
                                }

                                // TODO: We may want to add padding or spacer when the snackbar is shown so that
                                //  content doesn't display behind it.
                            }
                        }




                        HideContentBox(uiState.contentHidden)
                    }
                }


            }
        }
    }

}

@Composable
private fun NiaNavRail(
    destinations: List<TopLevelDestination>,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    NiaNavigationRail(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            val hasUnread = destinationsWithUnreadResources.contains(destination)
            NiaNavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.iconTextId)) },
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
            )
        }
    }
}

@Composable
private fun NiaBottomBar(
    item: List<MainModule.NavigationViewItem>,
    destinations: List<TopLevelDestination>,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)?,
) {
    NiaNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEachIndexed { index, destination ->
            val hasUnread = destinationsWithUnreadResources.contains(destination)
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            val enabled = item[index].enabled
            NiaNavigationBarItem(
                enabled = enabled,
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(destination.iconTextId)) },
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,
                onLongClick = onLongClick
            )
        }
    }
}

private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // This is based on the dimensions of the NavigationBar's "indicator pill";
                // however, its parameters are private, so we must depend on them implicitly
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                center = center + Offset(
                    64.dp.toPx() * .45f,
                    32.dp.toPx() * -.45f - 6.dp.toPx(),
                ),
            )
        }
    }

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false