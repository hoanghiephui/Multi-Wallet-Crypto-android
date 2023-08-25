package io.horizontalsystems.bankwallet.material.module.setting

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.modules.blockchainsettings.BlockchainSettingsScreen
import io.horizontalsystems.bankwallet.modules.btcblockchainsettings.BtcBlockchainSettingsScreen
import io.horizontalsystems.bankwallet.modules.btcblockchainsettings.BtcBlockchainSettingsViewModel

/**
 * BlockchainSettingsRouter
 */
@Composable
fun BlockchainSettingsRouter(navController: NavController) {
    BlockchainSettingsScreen(
        navController = navController,
    )
}

/**
 * BtcBlockchainSettingsRouter
 */
@Composable
fun BtcBlockchainSettingsRouter(
    navController: NavController,
    viewModel: BtcBlockchainSettingsViewModel = hiltViewModel()
) {
    BtcBlockchainSettingsScreen(
        viewModel = viewModel,
        navController = navController,
    )
}