package io.horizontalsystems.bankwallet.material.module.market

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import coin.chain.crypto.core.designsystem.theme.NiaTheme
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.market.topcoins.MarketTopCoinsViewModel
import io.horizontalsystems.bankwallet.modules.market.topcoins.TopCoinsScreen

const val marketTopCoinsNavigationRoute = "marketTopCoins_route"

fun NavController.navigateToMarketTopCoinsScreen(navOptions: NavOptions? = null) {
    this.navigate(marketTopCoinsNavigationRoute, navOptions)
}
fun NavController.navigateToMarketTopCoinsScreen(navOptions: NavOptions? = null, bundle: Bundle) {
    this.navigate(marketTopCoinsNavigationRoute, bundle, navOptions)
}

fun NavGraphBuilder.marketTopCoinsScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = marketTopCoinsNavigationRoute) {
        MarketTopCoinsRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun MarketTopCoinsRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: MarketTopCoinsViewModel = hiltViewModel()
) {
    NiaTheme {
        TopCoinsScreen(
            viewModel,
            { navController.popBackStack() },
            { coinUid ->

            }
        )
    }

}