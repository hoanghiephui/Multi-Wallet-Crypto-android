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

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import io.horizontalsystems.bankwallet.material.module.market.MarketRoute

const val LINKED_NEWS_RESOURCE_ID = "linkedNewsResourceId"
const val marketNavigationRoute = "markets_route/{$LINKED_NEWS_RESOURCE_ID}"
private const val DEEP_LINK_URI_PATTERN =
    "https://www.nowinandroid.apps.samples.google.com/foryou/{$LINKED_NEWS_RESOURCE_ID}"
const val MARKET_GRAPH_ROUTE_PATTERN = "markets_graph"


fun NavController.navigateToMarketGraph(navOptions: NavOptions? = null) {
    this.navigate(MARKET_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.marketGraph(
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = MARKET_GRAPH_ROUTE_PATTERN,
        startDestination = marketNavigationRoute,
    ) {
        composable(
            route = marketNavigationRoute,
            deepLinks = listOf(
                navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
            ),
            arguments = listOf(
                navArgument(LINKED_NEWS_RESOURCE_ID) { type = NavType.StringType },
            ),
        ) {
            MarketRoute(navController)
        }
        nestedGraphs()
    }

}