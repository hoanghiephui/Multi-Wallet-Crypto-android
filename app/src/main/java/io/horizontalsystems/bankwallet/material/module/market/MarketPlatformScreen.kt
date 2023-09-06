package io.horizontalsystems.bankwallet.material.module.market

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.market.platform.MarketPlatformFragment.Companion.platformKey
import io.horizontalsystems.bankwallet.modules.market.platform.MarketPlatformModule
import io.horizontalsystems.bankwallet.modules.market.platform.PlatformScreen
import io.horizontalsystems.bankwallet.modules.market.topplatforms.Platform
import io.horizontalsystems.core.parcelable

const val marketPlatformRoute = "marketPlatform_route"
fun NavController.navigateToMarketPlatformScreen(
    navOptions: NavOptions? = null,
    bundle: Bundle
) {
    this.navigate(marketPlatformRoute, bundle, navOptions)
}

fun NavGraphBuilder.marketPlatformScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = marketPlatformRoute) {
        MarketPlatformRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun MarketPlatformRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val platform = navController.currentBackStackEntry?.arguments?.parcelable<Platform>(
        platformKey
    ) ?: return
    val factory = MarketPlatformModule.Factory(platform)
    PlatformScreen(
        factory = factory,
        onCloseButtonClick = { navController.popBackStack() },
        onCoinClick = { coinUid ->
            val arguments = CoinFragment.prepareParams(coinUid)

        }
    )
}