package io.horizontalsystems.bankwallet.material.module.info

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.modules.info.InfoSourceScreen

@Composable
fun BtcBlockchainRestoreSourceInfoRouter(navController: NavController) {
    InfoSourceScreen(navController)
}