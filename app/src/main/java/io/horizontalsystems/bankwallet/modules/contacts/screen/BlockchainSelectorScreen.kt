package io.horizontalsystems.bankwallet.modules.contacts.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.cell.CellBlockchainChecked
import io.horizontalsystems.bankwallet.ui.compose.components.cell.SectionUniversalLawrence
import io.horizontalsystems.marketkit.models.Blockchain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockchainSelectorScreen(
    blockchains: List<Blockchain>,
    selectedBlockchain: Blockchain,
    onSelectBlockchain: (Blockchain) -> Unit,
    onNavigateToBack: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(selectedBlockchain) }
    ComposeAppTheme {
        Scaffold(
            topBar = {
                AppBar(
                    title = stringResource(R.string.Market_Filter_Blockchains),
                    navigationIcon = {
                        HsBackButton(onNavigateToBack)
                    },
                )
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(12.dp))
                SectionUniversalLawrence {
                    blockchains.forEachIndexed { index, item ->
                        CellBlockchainChecked(
                            borderTop = index != 0,
                            blockchain = item,
                            checked = selectedItem == item
                        ) {
                            selectedItem = item
                            onSelectBlockchain(item)
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
