package io.horizontalsystems.bankwallet.modules.balance.token

import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.entities.Wallet
import io.horizontalsystems.bankwallet.modules.transactions.TransactionsModule
import io.horizontalsystems.bankwallet.modules.transactions.TransactionsViewModel
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground

class TokenBalanceFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        withInput<Wallet>(navController) { wallet ->
            wallet?.let {
                val viewModel by viewModels<TokenBalanceViewModel> {
                    TokenBalanceModule.Factory(
                        wallet
                    )
                }
                val transactionsViewModel by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }
                NiaBackground {
                    TokenBalanceScreen(
                        viewModel,
                        transactionsViewModel,
                        navController
                    )
                }
            } ?: run {
                navController.popBackStack()
            }
        }
    }

    override val logScreen: String
        get() = "TokenBalanceFragment"

}
