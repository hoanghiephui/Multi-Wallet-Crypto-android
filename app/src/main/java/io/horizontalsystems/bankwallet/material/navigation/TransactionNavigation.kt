package io.horizontalsystems.bankwallet.material.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.module.transaction.TransactionRouter

const val transactionRoute = "transactions_route"

fun NavController.navigateToTransaction(navOptions: NavOptions? = null) {
    this.navigate(transactionRoute, navOptions)
}

fun NavGraphBuilder.transactionScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = transactionRoute) {
        TransactionRouter()
    }
}