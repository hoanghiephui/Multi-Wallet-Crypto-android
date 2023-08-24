package io.horizontalsystems.bankwallet.material.module.market

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewScreen

@Composable
fun MarketRoute() {
    val navController = rememberNavController()
    MarketOverviewScreen(navController)
}