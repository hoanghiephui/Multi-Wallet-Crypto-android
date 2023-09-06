package io.horizontalsystems.bankwallet.material.module.market

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.chart.ChartViewModel
import io.horizontalsystems.bankwallet.modules.market.category.CategoryScreen
import io.horizontalsystems.bankwallet.modules.market.category.MarketCategoryFragment.Companion.categoryKey
import io.horizontalsystems.bankwallet.modules.market.category.MarketCategoryModule
import io.horizontalsystems.bankwallet.modules.market.category.MarketCategoryViewModel
import io.horizontalsystems.core.parcelable
import io.horizontalsystems.marketkit.models.CoinCategory

const val marketCategoryNavigationRoute = "marketCategory_route"
fun NavController.navigateToMarketCategoryScreen(navOptions: NavOptions? = null, bundle: Bundle) {
    this.navigate(marketCategoryNavigationRoute, bundle, navOptions)
}

fun NavGraphBuilder.marketCategoryScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = marketCategoryNavigationRoute) {
        MarketCategoryRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun MarketCategoryRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val data = navController.currentBackStackEntry?.arguments?.parcelable<CoinCategory>(
        categoryKey
    ) ?: return
    val factory = MarketCategoryModule.Factory(
        data
    )
    val chartViewModel: ChartViewModel = viewModel<ChartViewModel>(factory = factory)
    val viewModel = viewModel<MarketCategoryViewModel>(factory = factory)
    CategoryScreen(
        viewModel,
        chartViewModel,
        { navController.popBackStack() },
        { coinUid -> }
    )
}