package io.horizontalsystems.bankwallet.modules.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coin.chain.crypto.core.designsystem.component.TopAppBarClose
import coin.chain.crypto.core.designsystem.theme.NiaTheme
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.modules.info.ui.InfoBody
import io.horizontalsystems.bankwallet.modules.info.ui.InfoHeader
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem
import io.horizontalsystems.core.findNavController

class EvmBlockchainSyncModeInfoFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            EvmNetworkInfoScreen(
                findNavController()
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvmNetworkInfoScreen(
    navController: NavController
) {

    NiaTheme {
        Column {
            TopAppBarClose(
                actionIcon = Icons.Rounded.Close,
                actionIconContentDescription = "ArrowBack",
                onActionClick = { navController.popBackStack() },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                InfoHeader(R.string.AddEvmSyncSource_RpcSource)
                InfoBody(R.string.EvmNetwork_SyncModeDescription)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
