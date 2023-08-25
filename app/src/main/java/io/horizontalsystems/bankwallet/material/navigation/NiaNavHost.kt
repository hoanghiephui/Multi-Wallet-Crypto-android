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

package io.horizontalsystems.bankwallet.material.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.material.main.NiaAppState
import io.horizontalsystems.bankwallet.material.module.info.btcBlockchainRestoreSourceInfoScreen
import io.horizontalsystems.bankwallet.material.module.setting.navigations.blockchainSettingsScreen
import io.horizontalsystems.bankwallet.material.module.setting.navigations.btcBlockchainSettingsScreen
import io.horizontalsystems.bankwallet.material.module.setting.navigations.donateScreen
import io.horizontalsystems.bankwallet.material.module.setting.navigations.manageAccountsScreen

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun NiaNavHost(
    appState: NiaAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = marketNavigationRoute,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        marketScreen(onTopicClick = {})
        balanceScreen(
            onTopicClick = { _ -> },
            onShowSnackbar = onShowSnackbar
        )
        transactionScreen(
            onShowSnackbar = onShowSnackbar
        )
        settingsGraph(
            navController = navController,
            nestedGraphs = {
                donateScreen(onBackPress = navController::popBackStack)
                manageAccountsScreen(
                    onBackPress = navController::popBackStack,
                    navController = navController
                )
                blockchainSettingsScreen(navController)
                btcBlockchainSettingsScreen(navController)
                btcBlockchainRestoreSourceInfoScreen(navController)
            },
        )
    }
}

val navOptionsSlideFromRight = NavOptions.Builder()
    .setEnterAnim(R.anim.slide_from_right)
    .setExitAnim(android.R.anim.fade_out)
    .setPopEnterAnim(android.R.anim.fade_in)
    .setPopExitAnim(R.anim.slide_to_right)
    .build()

@SuppressLint("RestrictedApi")
fun NavController.navigate(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val routeLink =
        NavDeepLinkRequest.Builder.fromUri(NavDestination.createRoute(route).toUri()).build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions, navigatorExtras)
    } else {
        navigate(route, navOptions, navigatorExtras)
    }
}
