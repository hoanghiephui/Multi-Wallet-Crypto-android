package io.horizontalsystems.bankwallet.material.module.coin.indicators

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.modules.coin.indicators.IndicatorsScreen

const val indicatorsNavigationRoute = "Indicators_route"
fun NavController.navigateToIndicatorsScreen(navOptions: NavOptions? = null) {
    this.navigate(indicatorsNavigationRoute, navOptions)
}

fun NavGraphBuilder.indicatorsScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = indicatorsNavigationRoute) {
        IndicatorsRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun IndicatorsRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    IndicatorsScreen(navController)
}