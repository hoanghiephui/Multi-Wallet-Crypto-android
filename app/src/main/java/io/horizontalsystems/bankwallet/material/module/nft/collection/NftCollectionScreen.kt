package io.horizontalsystems.bankwallet.material.module.nft.collection

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.horizontalsystems.bankwallet.modules.nft.collection.NftCollectionFragment
import io.horizontalsystems.bankwallet.modules.nft.collection.NftCollectionModule
import io.horizontalsystems.bankwallet.modules.nft.collection.NftCollectionScreen
import io.horizontalsystems.bankwallet.modules.nft.collection.overview.NftCollectionOverviewViewModel
import io.horizontalsystems.marketkit.models.BlockchainType

@Composable
fun NFTCollectionRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val arguments = navController.currentBackStackEntry?.arguments
    val nftCollectionUid = arguments?.getString(NftCollectionFragment.collectionUidKey) ?: return
    val blockchainTypeString =
        arguments.getString(NftCollectionFragment.blockchainTypeKey) ?: return
    val blockchainType = BlockchainType.fromUid(blockchainTypeString)
    val viewModel: NftCollectionOverviewViewModel =
        viewModel(factory = NftCollectionModule.Factory(blockchainType, nftCollectionUid))
    NftCollectionScreen(
        navController,
        viewModel,
        onShowSnackbar
    )
}