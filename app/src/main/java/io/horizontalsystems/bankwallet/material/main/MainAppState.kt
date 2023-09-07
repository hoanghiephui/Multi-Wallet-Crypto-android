/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.horizontalsystems.bankwallet.material.main

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import coin.chain.crypto.core.data.util.NetworkMonitor
import coin.chain.crypto.ui.TrackDisposableJank
import io.horizontalsystems.bankwallet.material.navigation.TopLevelDestination
import io.horizontalsystems.bankwallet.material.navigation.TopLevelDestination.BALANCE
import io.horizontalsystems.bankwallet.material.navigation.TopLevelDestination.MARKETS
import io.horizontalsystems.bankwallet.material.navigation.TopLevelDestination.SETTINGS
import io.horizontalsystems.bankwallet.material.navigation.TopLevelDestination.TRANSACTIONS
import io.horizontalsystems.bankwallet.material.navigation.balanceRoute
import io.horizontalsystems.bankwallet.material.navigation.marketNavigationRoute
import io.horizontalsystems.bankwallet.material.navigation.navigateToBalance
import io.horizontalsystems.bankwallet.material.navigation.navigateToMarketGraph
import io.horizontalsystems.bankwallet.material.navigation.navigateToSettingsGraph
import io.horizontalsystems.bankwallet.material.navigation.navigateToTransaction
import io.horizontalsystems.bankwallet.material.navigation.settingsRoute
import io.horizontalsystems.bankwallet.material.navigation.transactionRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberNiaAppState(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): NiaAppState {
    NavigationTrackingSideEffect(navController)
    return remember(
        navController,
        coroutineScope,
        windowSizeClass,
        networkMonitor,
    ) {
        NiaAppState(
            navController,
            coroutineScope,
            windowSizeClass,
            networkMonitor,
        )
    }
}

@Stable
class NiaAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            marketNavigationRoute -> MARKETS
            balanceRoute -> BALANCE
            transactionRoute -> TRANSACTIONS
            settingsRoute -> SETTINGS
            else -> null
        }

    val shouldShowBottomBar: Boolean
        @Composable get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                && topLevelDestinations.find { it.name == currentTopLevelDestination?.name } != null

    val shouldShowNavRail: Boolean
        @Composable get() = !shouldShowBottomBar && topLevelDestinations.find { it.name == currentTopLevelDestination?.name } != null

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries


    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            when (topLevelDestination) {
                MARKETS -> navController.navigateToMarketGraph(topLevelNavOptions)
                BALANCE -> navController.navigateToBalance(topLevelNavOptions)
                TRANSACTIONS -> navController.navigateToTransaction(topLevelNavOptions)
                SETTINGS -> navController.navigateToSettingsGraph(topLevelNavOptions)
            }
        }
    }

    fun navigateToSearch() {
        //navController.navigateToSearch()
    }
}

/**
 * Stores information about navigation events to be used with JankStats
 */
@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    TrackDisposableJank(navController) { metricsHolder ->
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            metricsHolder.state?.putState("Navigation", destination.route.toString())
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}
