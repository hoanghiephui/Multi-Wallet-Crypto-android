package io.horizontalsystems.bankwallet.material.module.nft

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.module.nft.collection.NFTCollectionRouter
import io.horizontalsystems.bankwallet.material.navigation.navOptionsSlideFromBottom
import io.horizontalsystems.bankwallet.material.navigation.navigate

const val nftCollectionRouter = "nftCollectionRouter"

fun NavController.navigateToNftCollectionScreen(
    navOptions: NavOptions = navOptionsSlideFromBottom,
    bundle: Bundle
) {
    this.navigate(nftCollectionRouter, bundle, navOptions)
}

fun NavGraphBuilder.nftCollectionScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = nftCollectionRouter) {
        NFTCollectionRouter(
            navController,
            onShowSnackbar
        )
    }
}