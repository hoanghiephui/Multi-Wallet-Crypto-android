package io.horizontalsystems.bankwallet.modules.evmnetwork

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coin.chain.crypto.core.designsystem.component.TopAppBar
import coin.chain.crypto.core.designsystem.theme.NiaTheme
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.composablePopup
import io.horizontalsystems.bankwallet.core.imageUrl
import io.horizontalsystems.bankwallet.entities.EvmSyncSource
import io.horizontalsystems.bankwallet.material.module.setting.navigations.navigateToAddRpcPage
import io.horizontalsystems.bankwallet.material.module.setting.navigations.navigateToEvmNetworkInfoPage
import io.horizontalsystems.bankwallet.modules.btcblockchainsettings.BlockchainSettingCell
import io.horizontalsystems.bankwallet.modules.evmnetwork.addrpc.AddRpcModule
import io.horizontalsystems.bankwallet.modules.evmnetwork.addrpc.AddRpcScreen
import io.horizontalsystems.bankwallet.modules.info.EvmNetworkInfoScreen
import io.horizontalsystems.bankwallet.modules.walletconnect.list.ui.ActionsRow
import io.horizontalsystems.bankwallet.modules.walletconnect.list.ui.DraggableCardSimple
import io.horizontalsystems.bankwallet.modules.walletconnect.list.ui.getShape
import io.horizontalsystems.bankwallet.modules.walletconnect.list.ui.showDivider
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.HeaderText
import io.horizontalsystems.bankwallet.ui.compose.components.HsIconButton
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem
import io.horizontalsystems.bankwallet.ui.compose.components.RowUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.body_jacob
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah
import io.horizontalsystems.bankwallet.ui.compose.components.subhead2_grey
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.coroutines.launch

class EvmNetworkFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            EvmNetworkNavHost(
                requireArguments(),
                findNavController()
            )
        }
    }

}

private const val EvmNetworkPage = "evm_network"
const val EvmNetworkInfoPage = "evm_network_info"
const val AddRpcPage = "add_rpc"

@Composable
private fun EvmNetworkNavHost(
    arguments: Bundle,
    fragmentNavController: NavController
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = EvmNetworkPage,
    ) {
        composable(EvmNetworkPage) {
            /*EvmNetworkScreen(
                navController = navController,
                onBackPress = { navController.popBackStack() },
                onShowSnackbar = onShowSnackbar
            )*/
        }
        composablePopup(AddRpcPage) { AddRpcScreen(navController) }
        composablePopup(EvmNetworkInfoPage) { EvmNetworkInfoScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvmNetworkScreen(
    navController: NavController,
    onBackPress: () -> Unit,
    viewModel: EvmNetworkViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {

    var revealedCardId by remember { mutableStateOf<String?>(null) }
    val rememberCoroutineScope = rememberCoroutineScope()
    val message = stringResource(id = R.string.Hud_Removed)
    NiaTheme {
        Column {
            TopAppBar(
                titleRes = viewModel.title,
                navigationIcon = {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = viewModel.blockchain.type.imageUrl,
                            error = painterResource(R.drawable.ic_platform_placeholder_32)
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .size(24.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                actionIcon = Icons.Rounded.Close,
                onActionClick = {
                    onBackPress.invoke()
                },
                actionIconContentDescription = "Button_Close"
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
            ) {

                item {
                    HeaderText(stringResource(R.string.AddEvmSyncSource_RpcSource)) {
                        navController.navigateToEvmNetworkInfoPage()
                    }
                }

                item {
                    CellUniversalLawrenceSection(viewModel.viewState.defaultItems) { item ->
                        BlockchainSettingCell(item.name, item.url, item.selected) {
                            viewModel.onSelectSyncSource(item.syncSource)
                        }
                    }
                }

                if (viewModel.viewState.customItems.isNotEmpty()) {

                    CustomRpcListSection(
                        viewModel.viewState.customItems,
                        revealedCardId,
                        onClick = { syncSource ->
                            viewModel.onSelectSyncSource(syncSource)
                        },
                        onReveal = { id ->
                            if (revealedCardId != id) {
                                revealedCardId = id
                            }
                        },
                        onConceal = {
                            revealedCardId = null
                        }
                    ) {
                        viewModel.onRemoveCustomRpc(it)
                        rememberCoroutineScope.launch {
                            onShowSnackbar.invoke(message, null)
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(32.dp))
                    AddButton {
                        navController.navigateToAddRpcPage(AddRpcModule.args(viewModel.blockchain))
                    }
                }
            }
        }
    }
}

private fun LazyListScope.CustomRpcListSection(
    items: List<EvmNetworkViewModel.ViewItem>,
    revealedCardId: String?,
    onClick: (EvmSyncSource) -> Unit,
    onReveal: (String) -> Unit,
    onConceal: () -> Unit,
    onDelete: (EvmSyncSource) -> Unit
) {
    item {
        Spacer(Modifier.height(32.dp))
        HeaderText(
            stringResource(R.string.EvmNetwork_Added),
        )
    }
    itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
        val showDivider = showDivider(items.size, index)
        val shape = getShape(items.size, index)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ActionsRow(
                content = {
                    HsIconButton(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(88.dp),
                        onClick = { onDelete(item.syncSource) },
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle_minus_24),
                                tint = Color.Gray,
                                contentDescription = "delete",
                            )
                        }
                    )
                },
            )
            DraggableCardSimple(
                isRevealed = revealedCardId == item.id,
                cardOffset = 72f,
                onReveal = { onReveal(item.id) },
                onConceal = onConceal,
                content = {
                    RpcCell(
                        shape = shape,
                        showDivider = showDivider,
                        item = item,
                        onItemClick = onClick
                    )
                }
            )
        }
    }
}

@Composable
private fun AddButton(
    onClick: () -> Unit
) {
    CellUniversalLawrenceSection(
        listOf {
            RowUniversal(
                onClick = onClick,
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Spacer(Modifier.width(16.dp))
                body_jacob(
                    text = stringResource(R.string.EvmNetwork_AddNew)
                )
            }
        }
    )
}

@Composable
fun RpcCell(
    shape: Shape,
    showDivider: Boolean = false,
    item: EvmNetworkViewModel.ViewItem,
    onItemClick: (EvmSyncSource) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.onPrimary)
            .clickable {
                onItemClick.invoke(item.syncSource)
            },
        contentAlignment = Alignment.Center
    ) {
        if (showDivider) {
            Divider(
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val title = when {
                    item.name.isNotBlank() -> item.name
                    else -> stringResource(id = R.string.WalletConnect_Unnamed)
                }

                body_leah(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                subhead2_grey(text = item.url)
            }
            if (item.selected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_checkmark_20),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
            }
        }
    }
}
