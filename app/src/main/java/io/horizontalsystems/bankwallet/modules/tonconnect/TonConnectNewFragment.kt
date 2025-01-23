package io.horizontalsystems.bankwallet.modules.tonconnect

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.tonapps.wallet.data.tonconnect.entities.DAppRequestEntity
import io.horizontalsystems.bankwallet.core.BaseComposeFragment

class TonConnectNewFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {
        withInput<DAppRequestEntity>(navController) { input ->
            input?.let {
                TonConnectNewScreen(navController, input)
            } ?: run {
                navController.popBackStack()
            }
        }
    }

    override val logScreen: String
        get() = "TonConnectNewFragment"
}
