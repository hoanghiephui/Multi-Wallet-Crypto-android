package io.horizontalsystems.bankwallet.modules.manageaccount.showextendedkey

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.modules.info.ui.InfoBody
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem

class KeyAccountInfoFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        InfoScreen(navController)
    }

    override val logScreen: String
        get() = "KeyAccountInfoFragment"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoScreen(navController: NavController) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            AppBar(
                title = stringResource(R.string.ExtendedKey_Account),
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = { navController.popBackStack() }
                    )
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                InfoBody(R.string.ExtendedKey_AccountInfo)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
