package io.horizontalsystems.bankwallet.material.module.info

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.navigation.navOptionsSlideFromRight

const val btcBlockchainRestoreSourceInfoRoute = "BtcBlockchainRestoreSourceInfo_route"

fun NavController.navigateToBtcBlockchainRestoreSourceInfo(
    navOptions: NavOptions = navOptionsSlideFromRight,
) {
    this.navigate(btcBlockchainRestoreSourceInfoRoute, navOptions)
}

fun NavGraphBuilder.btcBlockchainRestoreSourceInfoScreen(
    navController: NavController
) {
    composable(route = btcBlockchainRestoreSourceInfoRoute) {
        BtcBlockchainRestoreSourceInfoRouter(navController)
    }
}