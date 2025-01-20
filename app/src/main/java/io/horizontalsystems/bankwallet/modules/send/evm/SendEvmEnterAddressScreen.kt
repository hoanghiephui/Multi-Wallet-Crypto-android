package io.horizontalsystems.bankwallet.modules.send.evm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.entities.Address
import io.horizontalsystems.bankwallet.entities.Wallet
import io.horizontalsystems.bankwallet.modules.address.AddressParserViewModel
import io.horizontalsystems.bankwallet.modules.address.HSAddressInput
import io.horizontalsystems.bankwallet.modules.evmfee.ButtonsGroupWithShade
import io.horizontalsystems.bankwallet.modules.sendtokenselect.PrefilledData
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonPrimaryYellow
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendEvmEnterAddressScreen(
    viewModel: SendEvmViewModel,
    navController: NavController,
    prefilledData: PrefilledData?,
    wallet: Wallet,
    paymentAddressViewModel: AddressParserViewModel,
    onNext: () -> Unit
) {
    val uiState = viewModel.uiState
    val addressError = uiState.addressError

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppBar(
                title = stringResource(R.string.Send_EnterAddress),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
            )
        },
        bottomBar = {
            ButtonsGroupWithShade {
                ButtonPrimaryYellow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    title = stringResource(R.string.Button_Next),
                    onClick = onNext,
                    enabled = uiState.canBeSendToAddress
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            HSAddressInput(
                modifier = Modifier.padding(horizontal = 16.dp),
                initial = prefilledData?.address?.let { Address(it) },
                tokenQuery = wallet.token.tokenQuery,
                coinCode = wallet.coin.code,
                error = addressError,
                textPreprocessor = paymentAddressViewModel,
                navController = navController
            ) {
                viewModel.onEnterAddress(it)
            }
        }
    }
}
