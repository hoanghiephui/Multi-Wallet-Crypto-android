package io.horizontalsystems.bankwallet.material.module.setting

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsScreen
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsViewModel

@Composable
fun ManageAccountsRouter(
    onBackPress: () -> Unit,
    navController: NavController,
    viewModel: ManageAccountsViewModel = hiltViewModel()
) {
    ManageAccountsScreen(
        navController = navController,
        viewModel = viewModel
    )
}
//TODO: chưa xử lý xong