package io.horizontalsystems.bankwallet.modules.nft.collection

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import coin.chain.crypto.core.designsystem.component.TopAppBarClose
import coin.chain.crypto.core.designsystem.theme.NiaTheme
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.modules.nft.collection.assets.NftCollectionAssetsScreen
import io.horizontalsystems.bankwallet.modules.nft.collection.events.NftCollectionEventsScreen
import io.horizontalsystems.bankwallet.modules.nft.collection.overview.NftCollectionOverviewScreen
import io.horizontalsystems.bankwallet.modules.nft.collection.overview.NftCollectionOverviewViewModel
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem
import io.horizontalsystems.bankwallet.ui.compose.components.TabItem
import io.horizontalsystems.bankwallet.ui.compose.components.Tabs
import io.horizontalsystems.bankwallet.ui.helpers.LinkHelper
import io.horizontalsystems.bankwallet.ui.helpers.TextHelper
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.coroutines.launch

class NftCollectionFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val uid = activity?.intent?.data?.getQueryParameter("uid")
        val blockchainTypeUidFromIntent = activity?.intent?.data?.getQueryParameter("blockchainTypeUid")
        if (uid != null) {
            activity?.intent?.data = null
        }
        val nftCollectionUid = requireArguments().getString(collectionUidKey, uid ?: "")
        val blockchainTypeString = requireArguments().getString(blockchainTypeKey, blockchainTypeUidFromIntent ?: "")
        val blockchainType = BlockchainType.fromUid(blockchainTypeString)

        val viewModel by navGraphViewModels<NftCollectionOverviewViewModel>(R.id.nftCollectionFragment) {
            NftCollectionModule.Factory(blockchainType, nftCollectionUid)
        }

        /*NftCollectionScreen(
            findNavController(),
            viewModel
        )*/
    }

    companion object {
        const val collectionUidKey = "collectionUid"
        const val blockchainTypeKey = "blockchainType"

        fun prepareParams(collectionUid: String, blockchainType: BlockchainType) =
            bundleOf(collectionUidKey to collectionUid, blockchainTypeKey to blockchainType.uid)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NftCollectionScreen(navController: NavController, viewModel: NftCollectionOverviewViewModel, onShowSnackbar: suspend (String, String?) -> Boolean,) {
    NiaTheme {
        val tabs = viewModel.tabs
        val pagerState = rememberPagerState(initialPage = 0) { tabs.size}
        val coroutineScope = rememberCoroutineScope()
        val view = LocalView.current
        val context = LocalContext.current

        Column {

            TopAppBarClose(
                actionIcon = Icons.Rounded.Close,
                actionIconContentDescription = "ArrowBack",
                onActionClick = {navController.popBackStack()},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                )
            )

            val selectedTab = tabs[pagerState.currentPage]
            val tabItems = tabs.map {
                TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
            }
            Tabs(tabItems, onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(it.ordinal)
                }
            })

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (tabs[page]) {
                    NftCollectionModule.Tab.Overview -> {
                        val copyText = stringResource(id = R.string.Hud_Text_Copied)
                        NftCollectionOverviewScreen(
                            viewModel,
                            onCopyText = {
                                TextHelper.copyText(it)
                                coroutineScope.launch {
                                    onShowSnackbar.invoke(copyText, null)
                                }
                            },
                            onOpenUrl = {
                                LinkHelper.openLinkInAppBrowser(context, it)
                            }
                        )
                    }

                    NftCollectionModule.Tab.Items -> {
                        NftCollectionAssetsScreen(
                            navController,
                            viewModel.blockchainType,
                            viewModel.collectionUid
                        )
                    }

                    NftCollectionModule.Tab.Activity -> {
                        NftCollectionEventsScreen(
                            navController,
                            viewModel.blockchainType,
                            viewModel.collectionUid,
                            viewModel.contracts
                        )
                    }
                }
            }
        }
    }
}
