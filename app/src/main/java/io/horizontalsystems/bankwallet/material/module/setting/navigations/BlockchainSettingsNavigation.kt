package io.horizontalsystems.bankwallet.material.module.setting.navigations

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.core.composablePopup
import io.horizontalsystems.bankwallet.material.module.setting.BlockchainSettingsRouter
import io.horizontalsystems.bankwallet.material.module.setting.BtcBlockchainSettingsRouter
import io.horizontalsystems.bankwallet.material.module.setting.EvmSettingsRouter
import io.horizontalsystems.bankwallet.material.navigation.navOptionsSlideFromRight
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.evmnetwork.AddRpcPage
import io.horizontalsystems.bankwallet.modules.evmnetwork.EvmNetworkInfoPage
import io.horizontalsystems.bankwallet.modules.evmnetwork.addrpc.AddRpcScreen
import io.horizontalsystems.bankwallet.modules.info.EvmNetworkInfoScreen

const val blockchainSettingsRoute = "blockchainSettings_route"
const val btcBlockchainSettingsRoute = "btcBlockchainSettings_route"
const val evmSettingsRoute = "evmSettings_route"

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

/**
 * Evm setting
 */
fun NavController.navigateToEvmSettings(
    bundle: Bundle,
    navOptions: NavOptions = navOptionsSlideFromRight,
) {
    this.navigate(evmSettingsRoute, bundle, navOptions)
}

fun NavController.navigateToEvmNetworkInfoPage(
    navOptions: NavOptions = navOptionsSlideFromRight,
) {
    this.navigate(EvmNetworkInfoPage, navOptions)
}

fun NavController.navigateToAddRpcPage(
    bundle: Bundle,
    navOptions: NavOptions = navOptionsSlideFromRight,
) {
    this.navigate(AddRpcPage, bundle, navOptions)
}

fun NavGraphBuilder.evmSettingsScreen(
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = evmSettingsRoute) {
        EvmSettingsRouter(
            navController,
            onShowSnackbar
        )
    }

    composablePopup(AddRpcPage) { AddRpcScreen(navController) }
    composablePopup(EvmNetworkInfoPage) { EvmNetworkInfoScreen(navController) }
}