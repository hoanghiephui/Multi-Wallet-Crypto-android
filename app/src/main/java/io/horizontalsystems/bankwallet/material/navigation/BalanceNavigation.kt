package io.horizontalsystems.bankwallet.material.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.module.balance.BalanceRouter

const val balanceRoute = "balance_route"

fun NavController.navigateToBalance(navOptions: NavOptions? = null) {
    this.navigate(balanceRoute, navOptions)
}

fun NavGraphBuilder.balanceScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(
        route = balanceRoute,
    ) {
        BalanceRouter(
            navController,
            onShowSnackbar
        )
    }
}