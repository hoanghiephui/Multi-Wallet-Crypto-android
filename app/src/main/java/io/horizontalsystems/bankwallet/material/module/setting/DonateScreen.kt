package io.horizontalsystems.bankwallet.material.module.setting

import androidx.compose.runtime.Composable
import io.horizontalsystems.bankwallet.modules.settings.donate.DonateScreen

@Composable
fun DonateRouter(
    onBackPress: () -> Unit
) {
    DonateScreen(onBackPress)
}