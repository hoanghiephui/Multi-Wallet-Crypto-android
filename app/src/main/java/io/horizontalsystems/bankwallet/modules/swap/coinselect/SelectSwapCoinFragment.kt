package io.horizontalsystems.bankwallet.modules.swap.coinselect

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.badge
import io.horizontalsystems.bankwallet.core.getInput
import io.horizontalsystems.bankwallet.core.iconPlaceholder
import io.horizontalsystems.bankwallet.core.imageUrl
import io.horizontalsystems.bankwallet.core.setNavigationResultX
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule.CoinBalanceItem
import io.horizontalsystems.bankwallet.ui.compose.components.B2
import io.horizontalsystems.bankwallet.ui.compose.components.Badge
import io.horizontalsystems.bankwallet.ui.compose.components.CoinImage
import io.horizontalsystems.bankwallet.ui.compose.components.D1
import io.horizontalsystems.bankwallet.ui.compose.components.MultitextM1
import io.horizontalsystems.bankwallet.ui.compose.components.RowUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.SearchBar
import io.horizontalsystems.bankwallet.ui.compose.components.SectionUniversalItem
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer

class SelectSwapCoinFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        val dex = navController.getInput<SwapMainModule.Dex>()
        if (dex == null) {
            navController.popBackStack()
        } else {
            val viewModel = viewModel<SelectSwapCoinViewModel>(
                factory = SelectSwapCoinModule.Factory(
                    dex
                )
            )
            SelectSwapCoinDialogScreen(
                coinBalanceItems = viewModel.coinItems,
                onSearchTextChanged = viewModel::onEnterQuery,
                onClose = navController::popBackStack
            ) {
                navController.setNavigationResultX(it)
                Handler(Looper.getMainLooper()).postDelayed({
                    navController.popBackStack()
                }, 100)
            }
        }
    }

    override val logScreen: String
        get() = "SelectSwapCoinFragment"
}

@Composable
fun SelectSwapCoinDialogScreen(
    coinBalanceItems: List<CoinBalanceItem>,
    onSearchTextChanged: (String) -> Unit,
    onClose: () -> Unit,
    onClickItem: (CoinBalanceItem) -> Unit
) {
    SearchBar(
        title = stringResource(R.string.Select_Coins),
        onSearchTextChanged = onSearchTextChanged,
        hint = stringResource(R.string.ManageCoins_Search),
        navigationAction = onClose,
        content = {
            LazyColumn {
                items(coinBalanceItems) { coinItem ->
                    SectionUniversalItem(borderTop = true) {
                        RowUniversal(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onClick = {
                                onClickItem.invoke(coinItem)
                            }
                        ) {
                            CoinImage(
                                iconUrl = coinItem.token.coin.imageUrl,
                                placeholder = coinItem.token.iconPlaceholder,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            MultitextM1(
                                title = {
                                    Row {
                                        B2(text = coinItem.token.coin.name)
                                        coinItem.token.badge?.let {
                                            Badge(text = it)
                                        }
                                    }
                                },
                                subtitle = { D1(text = coinItem.token.coin.code) }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            MultitextM1(
                                title = {
                                    coinItem.balance?.let {
                                        App.numberFormatter.formatCoinFull(
                                            it,
                                            coinItem.token.coin.code,
                                            8
                                        )
                                    }?.let {
                                        B2(text = it)
                                    }
                                },
                                subtitle = {
                                    coinItem.fiatBalanceValue?.let { fiatBalanceValue ->
                                        App.numberFormatter.formatFiatFull(
                                            fiatBalanceValue.value,
                                            fiatBalanceValue.currency.symbol
                                        )
                                    }?.let {
                                        D1(
                                            modifier = Modifier.align(Alignment.End),
                                            text = it
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
                item {
                    VSpacer(height = 32.dp)
                }
            }
        }
    )
}
