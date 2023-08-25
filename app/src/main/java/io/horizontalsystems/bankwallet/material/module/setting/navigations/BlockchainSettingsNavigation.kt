package io.horizontalsystems.bankwallet.material.module.setting.navigations

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.module.setting.BlockchainSettingsRouter
import io.horizontalsystems.bankwallet.material.module.setting.BtcBlockchainSettingsRouter
import io.horizontalsystems.bankwallet.material.navigation.navOptionsSlideFromRight
import io.horizontalsystems.bankwallet.material.navigation.navigate

const val blockchainSettingsRoute = "blockchainSettings_route"
const val btcBlockchainSettingsRoute = "btcBlockchainSettings_route"

fun NavController.navigateToBlockchainSettings(
    navOptions: NavOptions = navOptionsSlideFromRight,
) {
    this.navigate(blockchainSettingsRoute, navOptions)
}

fun NavGraphBuilder.blockchainSettingsScreen(
    navController: NavController
) {
    composable(route = blockchainSettingsRoute) {
        BlockchainSettingsRouter(navController)
    }
}

/**
 * Navigation to BTC setting
 */
fun NavController.navigateToBtcBlockchainSettings(
    bundle: Bundle,
    navOptions: NavOptions = navOptionsSlideFromRight,
) {
    this.navigate(btcBlockchainSettingsRoute, bundle, navOptions)
}

fun NavGraphBuilder.btcBlockchainSettingsScreen(
    navController: NavController
) {
    composable(route = btcBlockchainSettingsRoute) {
        BtcBlockchainSettingsRouter(navController)
    }
}