package io.horizontalsystems.bankwallet.material.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.horizontalsystems.bankwallet.material.module.setting.SettingsRouter

private const val SETTINGS_GRAPH_ROUTE_PATTERN = "settings_graph"
const val settingsRoute = "settings_route"

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = SETTINGS_GRAPH_ROUTE_PATTERN,
        startDestination = settingsRoute,
    ) {
        composable(route = settingsRoute) {
            SettingsRouter(navController)
        }
        nestedGraphs()
    }
}