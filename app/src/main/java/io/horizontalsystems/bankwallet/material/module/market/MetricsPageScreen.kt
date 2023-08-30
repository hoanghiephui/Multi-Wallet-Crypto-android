package io.horizontalsystems.bankwallet.material.module.market

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.chart.ChartViewModel
import io.horizontalsystems.bankwallet.modules.market.metricspage.MetricsPage
import io.horizontalsystems.bankwallet.modules.market.metricspage.MetricsPageFragment
import io.horizontalsystems.bankwallet.modules.market.metricspage.MetricsPageModule
import io.horizontalsystems.bankwallet.modules.market.metricspage.MetricsPageViewModel
import io.horizontalsystems.bankwallet.modules.metricchart.MetricsType
import io.horizontalsystems.core.parcelable

const val metricsPageNavigationRoute = "metricsPage_route"

fun NavController.navigateToMetricsPageScreen(navOptions: NavOptions? = null, bundle: Bundle) {
    this.navigate(metricsPageNavigationRoute, bundle, navOptions)
}

fun NavGraphBuilder.metricsPageScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = metricsPageNavigationRoute) {
        MetricsPageRouter(
            navController,
            onShowSnackbar
        )
    }
}


@Composable
fun MetricsPageRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    ) {
    val data = navController.currentBackStackEntry?.arguments?.parcelable<MetricsType>(
        MetricsPageFragment.METRICS_TYPE_KEY
    ) ?: return
    val vmFactory: ViewModelProvider.Factory = MetricsPageModule.Factory(data)
    val chartViewModel: ChartViewModel = viewModel(factory = vmFactory)
    val viewModel: MetricsPageViewModel = viewModel(factory = vmFactory)
    MetricsPage(
        viewModel = viewModel,
        chartViewModel = chartViewModel,
        navController = navController,
        onCoinClick = {

        }
    )
}