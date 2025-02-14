package io.horizontalsystems.bankwallet.modules.balance.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.entities.AccountType
import io.horizontalsystems.bankwallet.modules.balance.BalanceAccountsViewModel
import io.horizontalsystems.bankwallet.modules.balance.BalanceModule
import io.horizontalsystems.bankwallet.modules.balance.BalanceScreenState
import io.horizontalsystems.bankwallet.modules.balance.cex.BalanceForAccountCex
import io.horizontalsystems.bankwallet.modules.main.MainViewModel

@Composable
fun BalanceScreen(navController: NavController,
                  mainViewModel: MainViewModel,) {
    val viewModel = viewModel<BalanceAccountsViewModel>(factory = BalanceModule.AccountsFactory())

    when (val tmpAccount = viewModel.balanceScreenState) {
        BalanceScreenState.NoAccount -> StartSetupWalletScreen(navController)
        is BalanceScreenState.HasAccount -> when (tmpAccount.accountViewItem.type) {
            is AccountType.Cex -> {
                BalanceForAccountCex(navController, tmpAccount.accountViewItem)
            }
            else -> {
                BalanceForAccount(navController, tmpAccount.accountViewItem, mainViewModel)
            }
        }
        else -> {}
    }
}