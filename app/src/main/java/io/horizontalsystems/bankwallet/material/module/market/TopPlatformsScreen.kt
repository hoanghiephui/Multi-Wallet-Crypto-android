package io.horizontalsystems.bankwallet.material.module.market

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.market.topplatforms.TopPlatformsScreen
import io.horizontalsystems.bankwallet.modules.market.topplatforms.TopPlatformsViewModel

const val marketTopPlatformsRoute = "marketTopPlatforms_route"
fun NavController.navigateToMarketTopPlatformsScreen(
    navOptions: NavOptions? = null,
    bundle: Bundle
) {
    this.navigate(marketTopPlatformsRoute, bundle, navOptions)
}

fun NavGraphBuilder.marketTopPlatformsScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = marketTopPlatformsRoute) {
        MarketTopPlatformsRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun MarketTopPlatformsRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: TopPlatformsViewModel = hiltViewModel()
) {
    TopPlatformsScreen(
        viewModel, navController
    )
}