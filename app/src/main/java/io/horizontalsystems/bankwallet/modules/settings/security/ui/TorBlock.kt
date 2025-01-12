package io.horizontalsystems.bankwallet.modules.settings.security.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.paidAction
import io.horizontalsystems.bankwallet.modules.settings.privacy.tor.SecurityTorSettingsViewModel
import io.horizontalsystems.bankwallet.modules.settings.security.SecurityCenterCell
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.HsSwitch
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah
import io.horizontalsystems.subscriptions.core.Tor

@Composable
fun TorBlock(
    viewModel: SecurityTorSettingsViewModel,
    navController: NavController,
    showAppRestartAlert: () -> Unit,
) {
    if (viewModel.showRestartAlert) {
        showAppRestartAlert()
        viewModel.restartAppAlertShown()
    }

    SecurityCenterCell(
        start = {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_tor_connection_24),
                tint = ComposeAppTheme.colors.jacob,
                contentDescription = null,
            )
        },
        center = {
            body_leah(
                text = stringResource(R.string.Tor_Title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        end = {
            HsSwitch(
                checked = viewModel.torCheckEnabled,
                onCheckedChange = { checked ->
                    navController.paidAction(Tor) {
                        viewModel.setTorEnabledWithChecks(checked)
                    }
                }
            )
        }
    )
}
