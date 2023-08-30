package io.horizontalsystems.bankwallet.material.module.market

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.modules.market.tvl.TvlChartViewModel
import io.horizontalsystems.bankwallet.modules.market.tvl.TvlModule
import io.horizontalsystems.bankwallet.modules.market.tvl.TvlScreen
import io.horizontalsystems.bankwallet.modules.market.tvl.TvlViewModel

const val tvlNavigationRoute = "tvl_route"

fun NavController.navigateToTvlScreen(navOptions: NavOptions? = null) {
    this.navigate(tvlNavigationRoute, navOptions)
}

fun NavGraphBuilder.tvlScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = tvlNavigationRoute) {
        TvlRouter(
            navController,
            onShowSnackbar
        )
    }
}


@Composable
fun TvlRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    vmFactory: ViewModelProvider.Factory = TvlModule.Factory(),
    tvlChartViewModel: TvlChartViewModel = viewModel(factory = vmFactory),
    viewModel: TvlViewModel = viewModel(factory = vmFactory)
) {
    TvlScreen(
        tvlViewModel = viewModel,
        chartViewModel = tvlChartViewModel,
        onCoinClick = {

        },
        navController = navController
    )
}