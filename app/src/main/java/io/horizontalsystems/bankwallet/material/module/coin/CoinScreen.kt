package io.horizontalsystems.bankwallet.material.module.coin

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.coin.CoinScreen
import io.horizontalsystems.bankwallet.modules.coin.CoinViewModel

const val coinNavigationRoute = "coin_route"
fun NavController.navigateToCoinScreen(navOptions: NavOptions? = null, bundle: Bundle) {
    this.navigate(coinNavigationRoute, bundle, navOptions)
}

fun NavGraphBuilder.coinScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = coinNavigationRoute) {
        CoinRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun CoinRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    coinViewModel: CoinViewModel = hiltViewModel()
) {
    val coinUid: String = coinViewModel.coinUid
    CoinScreen(
        coinUid = coinUid,
        navController = navController,
        coinViewModel = coinViewModel,
        onShowSnackbar = onShowSnackbar
    )
}