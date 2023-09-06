package io.horizontalsystems.bankwallet.material.module.market

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import io.horizontalsystems.bankwallet.material.navigation.navigate
import io.horizontalsystems.bankwallet.modules.market.topnftcollections.TopNftCollectionsScreen
import io.horizontalsystems.bankwallet.modules.market.topnftcollections.TopNftCollectionsViewModel
import io.horizontalsystems.bankwallet.modules.nft.collection.NftCollectionFragment

const val marketTopNftCollectionsRoute = "marketTopNftCollections_route"
fun NavController.navigateToMarketTopNftCollectionsScreen(
    navOptions: NavOptions? = null,
    bundle: Bundle
) {
    this.navigate(marketTopNftCollectionsRoute, bundle, navOptions)
}

fun NavGraphBuilder.marketTopNftCollectionsScreen(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = marketTopNftCollectionsRoute) {
        MarketTopNftCollectionsRouter(
            navController,
            onShowSnackbar
        )
    }
}

@Composable
fun MarketTopNftCollectionsRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: TopNftCollectionsViewModel = hiltViewModel()
) {
    TopNftCollectionsScreen(
        viewModel,
        onCloseButtonClick = { navController.popBackStack() },
        onClickCollection = { blockchainType, collectionUid ->
            val args = NftCollectionFragment.prepareParams(collectionUid, blockchainType)
        }
    )
}