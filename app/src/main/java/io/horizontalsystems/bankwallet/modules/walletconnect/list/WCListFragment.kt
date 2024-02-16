package io.horizontalsystems.bankwallet.modules.walletconnect.list

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.getInput
import io.horizontalsystems.bankwallet.modules.walletconnect.list.ui.WCSessionsScreen
import kotlinx.parcelize.Parcelize
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground

class WCListFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.getInput<Input>()
        NiaBackground {
            WCSessionsScreen(
                navController,
                input?.deepLinkUri
            )
        }
    }

    override val logScreen: String
        get() = "WCListFragment"

    @Parcelize
    data class Input(val deepLinkUri: String) : Parcelable
}
