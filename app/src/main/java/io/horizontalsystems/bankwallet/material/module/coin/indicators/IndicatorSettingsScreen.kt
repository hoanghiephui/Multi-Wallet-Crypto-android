package io.horizontalsystems.bankwallet.material.module.coin.indicators

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.chart.ChartIndicatorSetting
import io.horizontalsystems.bankwallet.modules.coin.indicators.EmaSettingsScreen
import io.horizontalsystems.bankwallet.modules.coin.indicators.MacdSettingsScreen
import io.horizontalsystems.bankwallet.modules.coin.indicators.RsiSettingsScreen

const val IndicatorSettingsNavigationRoute = " IndicatorSettings_route"
fun NavController.navigateToIndicatorSettingsScreen(navOptions: NavOptions? = null, bundle: Bundle) {
    this.navigate(IndicatorSettingsNavigationRoute, bundle, navOptions)
}

fun NavGraphBuilder.indicatorSettingsScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = IndicatorSettingsNavigationRoute) {
        IndicatorSettingsRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun IndicatorSettingsRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val arguments = navController.currentBackStackEntry?.arguments ?: return
    val indicatorSetting = arguments.getString("indicatorId")?.let {
        App.chartIndicatorManager.getChartIndicatorSetting(it)
    } ?: return
    when (indicatorSetting.type) {
        ChartIndicatorSetting.IndicatorType.MA -> {
            EmaSettingsScreen(
                navController = navController,
                indicatorSetting = indicatorSetting
            )
        }
        ChartIndicatorSetting.IndicatorType.RSI -> {
            RsiSettingsScreen(
                navController = navController,
                indicatorSetting = indicatorSetting
            )
        }
        ChartIndicatorSetting.IndicatorType.MACD -> {
            MacdSettingsScreen(
                navController = navController,
                indicatorSetting = indicatorSetting
            )
        }
    }
}