package io.horizontalsystems.bankwallet.material.module.setting.navigations

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.module.setting.DonateRouter

const val donateRoute = "donate_route"

fun NavController.navigateToDonate(navOptions: NavOptions? = null) {
    this.navigate(donateRoute, navOptions)
}

fun NavGraphBuilder.donateScreen(onBackPress: () -> Unit) {
    composable(route = donateRoute) {
        DonateRouter(onBackPress)
    }
}