package io.horizontalsystems.bankwallet.material.module.setting.navigations

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.module.setting.ManageAccountsRouter
import io.horizontalsystems.bankwallet.material.navigation.navOptionsSlideFromRight
import io.horizontalsystems.bankwallet.material.navigation.navigate

const val manageAccountsRoute = "manageAccounts_route"

fun NavController.navigateToManageAccounts(
    navOptions: NavOptions = navOptionsSlideFromRight,
    bundle: Bundle
) {
    this.navigate(manageAccountsRoute, bundle, navOptions)
}

fun NavGraphBuilder.manageAccountsScreen(
    onBackPress: () -> Unit,
    navController: NavController
) {
    composable(route = manageAccountsRoute) {
        ManageAccountsRouter(onBackPress, navController)
    }
}