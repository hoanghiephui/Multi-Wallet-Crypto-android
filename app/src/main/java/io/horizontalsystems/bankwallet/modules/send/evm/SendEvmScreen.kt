package io.horizontalsystems.bankwallet.modules.send.evm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.BuildConfig
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.analytics.TrackScreenViewEvent
import io.horizontalsystems.bankwallet.core.AdType
import io.horizontalsystems.bankwallet.core.MaxTemplateNativeAdViewComposable
import io.horizontalsystems.bankwallet.core.HSCaution
import io.horizontalsystems.bankwallet.core.badge
import io.horizontalsystems.bankwallet.core.slideFromRightForResult
import io.horizontalsystems.bankwallet.entities.Currency
import io.horizontalsystems.bankwallet.entities.Wallet
import io.horizontalsystems.bankwallet.modules.address.AddressParserViewModel
import io.horizontalsystems.bankwallet.modules.amount.AmountInputModeViewModel
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalance
import io.horizontalsystems.bankwallet.modules.multiswap.AmountInput
import io.horizontalsystems.bankwallet.modules.multiswap.FiatAmountInput
import io.horizontalsystems.bankwallet.modules.multiswap.SuggestionsBar
import io.horizontalsystems.bankwallet.modules.send.evm.confirmation.SendEvmConfirmationFragment
import io.horizontalsystems.bankwallet.modules.sendtokenselect.PrefilledData
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.Keyboard
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonPrimaryYellow
import io.horizontalsystems.bankwallet.ui.compose.components.CoinImage
import io.horizontalsystems.bankwallet.ui.compose.components.HSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.cell.CellUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.cell.SectionUniversalLawrence
import io.horizontalsystems.bankwallet.ui.compose.components.micro_grey
import io.horizontalsystems.bankwallet.ui.compose.components.subhead1_jacob
import io.horizontalsystems.bankwallet.ui.compose.components.subhead1_leah
import io.horizontalsystems.bankwallet.ui.compose.components.subhead2_grey
import io.horizontalsystems.bankwallet.ui.compose.observeKeyboardState
import io.horizontalsystems.core.helpers.HudHelper
import io.horizontalsystems.marketkit.models.Token
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendEvmScreen(
    title: String,
    navController: NavController,
    amountInputModeViewModel: AmountInputModeViewModel,
    prefilledData: PrefilledData?, //todo check if it is used
    wallet: Wallet,
    viewModel: SendEvmViewModel,
    paymentAddressViewModel: AddressParserViewModel,
    onBack: () -> Unit,
) {
    val uiState = viewModel.uiState

    val availableBalance = uiState.availableBalance
    val addressError = uiState.addressError
    val proceedEnabled = uiState.canBeSend
    val amountInputType = amountInputModeViewModel.inputType

    val amountUnique = paymentAddressViewModel.amountUnique
    val view = LocalView.current

    val focusManager = LocalFocusManager.current
    val keyboardState by observeKeyboardState()
    var amountInputHasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        amountUnique?.let {
            viewModel.onEnterAmount(it.amount)
        }
    }

    val nativeAd by viewModel.adState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(key1 = BuildConfig.SEND_COIN_NATIVE, block = {
        viewModel.loadAds(context, BuildConfig.SEND_COIN_NATIVE)
    })
    ComposeAppTheme {
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        Scaffold(
            topBar = {
                AppBar(
                    title = title,
                    navigationIcon = {
                        HsBackButton(onClick = { navController.popBackStack() })
                    },
                    menuItems = listOf(
                        MenuItem(
                            title = TranslatableString.ResString(R.string.Button_Close),
                            icon = R.drawable.ic_close,
                            onClick = {
                                navController.popBackStack()
                            }
                        )
                    )
                )
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.background,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (uiState.showAddressInput) {
                        SectionUniversalLawrence {
                            CellUniversal(
                                borderTop = false,
                                onClick = onBack
                            ) {
                                subhead2_grey(text = stringResource(R.string.Send_Confirmation_To))

                                HSpacer(16.dp)
                                subhead1_leah(
                                    modifier = Modifier.weight(1f),
                                    text = uiState.address?.hex ?: ""
                                )

                                HSpacer(16.dp)
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_down_arrow_20),
                                    contentDescription = null,
                                    tint = ComposeAppTheme.colors.grey
                                )
                            }
                        }

                        VSpacer(16.dp)
                    }

                    TokenAmountInput(
                        coinAmount = uiState.coinAmount,
                        fiatAmount = uiState.fiatAmount,
                        currency = uiState.currency,
                        amountCaution = uiState.amountCaution,
                        onValueChange = {
                            viewModel.onEnterAmount(it)
                            amountInputModeViewModel.onCoinInput()
                        },
                        onFiatValueChange = {
                            viewModel.onEnterFiatAmount(it)
                            amountInputModeViewModel.onFiatInput()
                        },
                        fiatAmountInputEnabled = viewModel.coinRate != null,
                        token = viewModel.wallet.token,
                        focusRequester = focusRequester,
                        onFocusChanged = { amountInputHasFocus = it.hasFocus }
                    )

                    VSpacer(8.dp)

                    AvailableBalance(
                        coinCode = wallet.coin.code,
                        coinDecimal = viewModel.coinMaxAllowedDecimals,
                        fiatDecimal = viewModel.fiatMaxAllowedDecimals,
                        availableBalance = availableBalance,
                        amountInputType = amountInputType,
                        rate = viewModel.coinRate
                    )

                    VSpacer(16.dp)
                    MaxTemplateNativeAdViewComposable(nativeAd, AdType.SMALL)
                    Spacer(modifier = Modifier.height(12.dp))
                    ButtonPrimaryYellow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        title = stringResource(R.string.Send_DialogProceed),
                        onClick = {
                            if (viewModel.hasConnection()) {
                                viewModel.getSendData()?.let {
                                    navController.slideFromRightForResult<SendEvmConfirmationFragment.Result>(
                                        R.id.sendEvmConfirmationFragment,
                                        SendEvmConfirmationFragment.Input(
                                            sendData = it,
                                            blockchainType = viewModel.wallet.token.blockchainType
                                        )
                                    ) {
                                        if (it.success) {
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            } else {
                                HudHelper.showErrorMessage(view, R.string.Hud_Text_NoInternet)
                            }
                        },
                        enabled = proceedEnabled
                    )
                    VSpacer(24.dp)
                }

                if (amountInputHasFocus && keyboardState == Keyboard.Opened) {
                    val hasNonZeroBalance = uiState.availableBalance > BigDecimal.ZERO
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            // Add IME (keyboard) padding to push content above keyboard
                            .windowInsetsPadding(WindowInsets.ime)
                            .systemBarsPadding()
                    ) {
                        SuggestionsBar(
                            onDelete = {
                                viewModel.onEnterAmount(null)
                            },
                            onSelect = {
                                focusManager.clearFocus()
                                viewModel.onEnterAmountPercentage(it)
                            },
                            selectEnabled = hasNonZeroBalance,
                            deleteEnabled = uiState.coinAmount != null,
                        )
                    }
                }
            }
        }
    }
    TrackScreenViewEvent("SendEvmScreen")
}

@Composable
private fun TokenAmountInput(
    coinAmount: BigDecimal?,
    fiatAmount: BigDecimal?,
    currency: Currency,
    amountCaution: HSCaution?,
    onValueChange: (BigDecimal?) -> Unit,
    onFiatValueChange: (BigDecimal?) -> Unit,
    fiatAmountInputEnabled: Boolean,
    token: Token?,
    focusRequester: FocusRequester,
    onFocusChanged: (FocusState) -> Unit,
) {
    val borderColor = when (amountCaution?.type) {
        HSCaution.Type.Error -> ComposeAppTheme.colors.red50
        HSCaution.Type.Warning -> ComposeAppTheme.colors.yellow50
        else -> ComposeAppTheme.colors.steel20
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .background(ComposeAppTheme.colors.lawrence)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .onFocusChanged(onFocusChanged)
                .weight(1f)
        ) {
            AmountInput(
                value = coinAmount,
                onValueChange = onValueChange,
                focusRequester = focusRequester
            )
            VSpacer(height = 3.dp)
            FiatAmountInput(
                value = fiatAmount,
                currency = currency,
                onValueChange = onFiatValueChange,
                enabled = fiatAmountInputEnabled
            )
        }
        HSpacer(width = 8.dp)
        CoinDetails(token)
    }
}

@Composable
private fun CoinDetails(
    token: Token?,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinImage(
            token = token,
            modifier = Modifier.size(32.dp)
        )
        HSpacer(width = 8.dp)
        if (token != null) {
            Column {
                subhead1_leah(text = token.coin.code)
                VSpacer(height = 1.dp)
                micro_grey(text = token.badge ?: stringResource(id = R.string.CoinPlatforms_Native))
            }
        } else {
            subhead1_jacob(text = stringResource(R.string.Swap_TokenSelectorTitle))
        }
    }
}