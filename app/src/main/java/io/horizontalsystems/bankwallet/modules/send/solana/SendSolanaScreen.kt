package io.horizontalsystems.bankwallet.modules.send.solana

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.BuildConfig
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.analytics.TrackScreenViewEvent
import io.horizontalsystems.bankwallet.core.AdType
import io.horizontalsystems.bankwallet.core.MaxTemplateNativeAdViewComposable
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.Address
import io.horizontalsystems.bankwallet.modules.address.AddressParserModule
import io.horizontalsystems.bankwallet.modules.address.AddressParserViewModel
import io.horizontalsystems.bankwallet.modules.address.HSAddressInput
import io.horizontalsystems.bankwallet.modules.amount.AmountInputModeViewModel
import io.horizontalsystems.bankwallet.modules.amount.HSAmountInput
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalance
import io.horizontalsystems.bankwallet.modules.send.SendConfirmationFragment
import io.horizontalsystems.bankwallet.modules.send.SendScreen
import io.horizontalsystems.bankwallet.modules.sendtokenselect.PrefilledData
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonPrimaryYellow
import io.horizontalsystems.core.helpers.HudHelper

@Composable
fun SendSolanaScreen(
    title: String,
    navController: NavController,
    viewModel: SendSolanaViewModel,
    amountInputModeViewModel: AmountInputModeViewModel,
    sendEntryPointDestId: Int,
    prefilledData: PrefilledData?,
) {
    val view = LocalView.current
    val wallet = viewModel.wallet
    val uiState = viewModel.uiState

    val availableBalance = uiState.availableBalance
    val addressError = uiState.addressError
    val amountCaution = uiState.amountCaution
    val proceedEnabled = uiState.canBeSend
    val amountInputType = amountInputModeViewModel.inputType
    val nativeAd by viewModel.adState
    val context = LocalContext.current
    LaunchedEffect(key1 = BuildConfig.SEND_COIN_NATIVE, block = {
        viewModel.loadAds(context, BuildConfig.SEND_COIN_NATIVE)
    })
    val paymentAddressViewModel = viewModel<AddressParserViewModel>(
        factory = AddressParserModule.Factory(wallet.token, prefilledData?.amount)
    )
    val amountUnique = paymentAddressViewModel.amountUnique

    ComposeAppTheme {
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        SendScreen(
            title = title,
            onCloseClick = { navController.popBackStack() }
        ) {
            AvailableBalance(
                    coinCode = wallet.coin.code,
                    coinDecimal = viewModel.coinMaxAllowedDecimals,
                    fiatDecimal = viewModel.fiatMaxAllowedDecimals,
                    availableBalance = availableBalance,
                    amountInputType = amountInputType,
                    rate = viewModel.coinRate
            )

            Spacer(modifier = Modifier.height(12.dp))
            HSAmountInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    focusRequester = focusRequester,
                    availableBalance = availableBalance,
                    caution = amountCaution,
                    coinCode = wallet.coin.code,
                    coinDecimal = viewModel.coinMaxAllowedDecimals,
                    fiatDecimal = viewModel.fiatMaxAllowedDecimals,
                    onClickHint = {
                        amountInputModeViewModel.onToggleInputType()
                    },
                    onValueChange = {
                        viewModel.onEnterAmount(it)
                    },
                    inputType = amountInputType,
                    rate = viewModel.coinRate,
                    amountUnique = amountUnique
            )

            if (uiState.showAddressInput) {
                Spacer(modifier = Modifier.height(12.dp))
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
            Spacer(modifier = Modifier.height(12.dp))
            MaxTemplateNativeAdViewComposable(nativeAd, AdType.SMALL)

            ButtonPrimaryYellow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                title = stringResource(R.string.Send_DialogProceed),
                onClick = {
                    if (viewModel.hasConnection()) {
                        navController.slideFromRight(
                            R.id.sendConfirmation,
                            SendConfirmationFragment.Input(
                                SendConfirmationFragment.Type.Solana,
                                sendEntryPointDestId
                            )
                        )
                    } else {
                        HudHelper.showErrorMessage(view, R.string.Hud_Text_NoInternet)
                    }
                },
                enabled = proceedEnabled
            )
        }
    }
    TrackScreenViewEvent("SendSolanaScreen")
}
