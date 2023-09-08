package io.horizontalsystems.bankwallet.material.module.nft.asset

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.entities.nft.NftUid
import io.horizontalsystems.bankwallet.material.navigation.navOptionsSlideFromBottom
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.nft.asset.NftAssetModule
import io.horizontalsystems.bankwallet.modules.nft.asset.NftAssetScreen

const val NftAssetRouter = "NftAssetRouter"

fun NavController.navigateToNftAssetScreen(
    navOptions: NavOptions = navOptionsSlideFromBottom,
    bundle: Bundle
) {
    this.navigate(NftAssetRouter, bundle, navOptions)
}

fun NavGraphBuilder.nftAssetScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = NftAssetRouter) {
        NftAssetRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun NftAssetRouter(
    navHostController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val argument = navHostController.currentBackStackEntry?.arguments
    val collectionUid = argument?.getString(NftAssetModule.collectionUidKey) ?: return
    val nftUid = argument.getString(NftAssetModule.nftUidKey)?.let { NftUid.fromUid(it) } ?: return
    NftAssetScreen(navHostController, collectionUid, nftUid, onShowSnackbar)
}