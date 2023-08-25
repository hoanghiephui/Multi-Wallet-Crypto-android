package io.horizontalsystems.bankwallet.modules.blockchainsettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coin.chain.crypto.core.designsystem.component.TopAppBar
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseFragment
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.material.module.setting.navigations.navigateToBtcBlockchainSettings
import io.horizontalsystems.bankwallet.modules.btcblockchainsettings.BtcBlockchainSettingsModule
import io.horizontalsystems.bankwallet.modules.evmnetwork.EvmNetworkModule
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.*
import io.horizontalsystems.core.findNavController

class BlockchainSettingsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                ComposeAppTheme {
                    BlockchainSettingsScreen(
                        navController = findNavController(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockchainSettingsScreen(
    navController: NavController,
    viewModel: BlockchainSettingsViewModel = viewModel(factory = BlockchainSettingsModule.Factory()),
) {

    Column {
        TopAppBar(
            titleRes = R.string.BlockchainSettings_Title,
            navigationIcon = Icons.Rounded.ArrowBack,
            navigationIconContentDescription = "ArrowBack",
            onNavigationClick = { navController.popBackStack() },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(12.dp))
            BlockchainSettingsBlock(
                btcLikeChains = viewModel.btcLikeChains,
                otherChains = viewModel.otherChains,
                navController = navController
            )
            Spacer(Modifier.height(44.dp))
        }
    }

}

@Composable
fun BlockchainSettingsBlock(
    btcLikeChains: List<BlockchainSettingsModule.BlockchainViewItem>,
    otherChains: List<BlockchainSettingsModule.BlockchainViewItem>,
    navController: NavController
) {
    CellUniversalLawrenceSection(btcLikeChains) { item ->
        BlockchainSettingCell(item) {
            onClick(item, navController)
        }
    }
    Spacer(Modifier.height(32.dp))
    CellUniversalLawrenceSection(otherChains) { item ->
        BlockchainSettingCell(item) {
            onClick(item, navController)
        }
    }
}

private fun onClick(
    item: BlockchainSettingsModule.BlockchainViewItem,
    navController: NavController
) {
    when (item.blockchainItem) {
        is BlockchainSettingsModule.BlockchainItem.Btc -> {
            val params = BtcBlockchainSettingsModule.args(item.blockchainItem.blockchain)
            navController.navigateToBtcBlockchainSettings(params)
        }
        is BlockchainSettingsModule.BlockchainItem.Evm -> {
            val params = EvmNetworkModule.args(item.blockchainItem.blockchain)
            navController.slideFromBottom(R.id.evmNetworkFragment, params)
        }
        is BlockchainSettingsModule.BlockchainItem.Solana -> {
            navController.slideFromBottom(R.id.solanaNetworkFragment, bundleOf())
        }
    }
}

@Composable
private fun BlockchainSettingCell(
    item: BlockchainSettingsModule.BlockchainViewItem,
    onClick: () -> Unit
) {
    RowUniversal(
        onClick = onClick
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(32.dp),
            painter = rememberAsyncImagePainter(
                model = item.imageUrl,
                error = painterResource(R.drawable.ic_platform_placeholder_32)
            ),
            contentDescription = null,
        )
        Column(modifier = Modifier.weight(1f)) {
            body_leah(text = item.title)
            subhead2_grey(text = item.subtitle)
        }
        Icon(
            modifier = Modifier.padding(horizontal = 16.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
