package io.horizontalsystems.bankwallet.modules.walletconnect.list

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.modules.walletconnect.list.ui.WCSessionsScreen
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground

class WCListFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val deepLinkUri = arguments?.getString(WC_CONNECTION_URI_KEY)
        NiaBackground {
            WCSessionsScreen(
                navController,
                deepLinkUri
            )
        }
    }

    companion object {
        private const val WC_CONNECTION_URI_KEY = "wc_connection_uri_key"

        fun prepareParams(deepLinkUri: String?) = bundleOf(WC_CONNECTION_URI_KEY to deepLinkUri)
    }

}
