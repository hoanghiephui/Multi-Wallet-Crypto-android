package io.horizontalsystems.bankwallet.modules.confirm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.modules.evmfee.ButtonsGroupWithShade
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmTransactionScreen(
    title: String = stringResource(R.string.Swap_Confirm_Title),
    onClickBack: () -> Unit,
    onClickSettings: (() -> Unit)?,
    onClickClose: (() -> Unit)?,
    buttonsSlot: @Composable (ColumnScope.() -> Unit),
    content: @Composable (ColumnScope.() -> Unit)
) {
    Scaffold(
        topBar = {
            AppBar(
                title = title,
                navigationIcon = {
                    HsBackButton(onClick = onClickBack)
                },
                menuItems = buildList<MenuItem> {
                    onClickSettings?.let {
                        add(
                            MenuItem(
                                title = TranslatableString.ResString(R.string.Settings_Title),
                                icon = R.drawable.ic_manage_2_24,
                                onClick = onClickSettings
                            )
                        )
                    }
                    onClickClose?.let {
                        add(
                            MenuItem(
                                title = TranslatableString.ResString(R.string.Button_Close),
                                icon = R.drawable.ic_close,
                                onClick = onClickClose
                            )
                        )
                    }
                },
            )
        },
        bottomBar = {
            ButtonsGroupWithShade {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp).navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = buttonsSlot
                )
            }
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            VSpacer(height = 12.dp)

            content.invoke(this)

            VSpacer(height = 32.dp)
        }
    }
}