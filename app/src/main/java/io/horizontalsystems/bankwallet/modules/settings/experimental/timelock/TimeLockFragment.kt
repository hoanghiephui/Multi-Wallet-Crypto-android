package io.horizontalsystems.bankwallet.modules.settings.experimental.timelock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.modules.settings.experimental.ActivateCell
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.InfoText
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground
import io.horizontalsystems.core.findNavController

class TimeLockFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            ExperimentalScreen(
                findNavController()
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExperimentalScreen(
    navController: NavController,
    viewModel: TimeLockViewModel = viewModel(factory = TimeLockModule.Factory())
) {
    NiaBackground {
        Column {
            AppBar(
                title = stringResource(R.string.BitcoinHodling_Title),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                }
            )
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(12.dp))
                ActivateCell(
                    checked = viewModel.timeLockActivated,
                    onChecked = { activated -> viewModel.setActivated(activated) }
                )
                InfoText(
                    text = stringResource(R.string.BitcoinHodling_Description)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
