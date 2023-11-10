package io.horizontalsystems.bankwallet.modules.managewallets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.configuredtoken.ConfiguredTokenInfoDialog
import io.horizontalsystems.bankwallet.modules.enablecoin.restoresettings.RestoreSettingsViewModel
import io.horizontalsystems.bankwallet.modules.enablecoin.restoresettings.ZCashConfig
import io.horizontalsystems.bankwallet.modules.restoreaccount.restoreblockchains.CoinViewItem
import io.horizontalsystems.bankwallet.modules.zcashconfigure.ZcashConfigure
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.*
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.getNavigationResult
import io.horizontalsystems.core.parcelable
import io.horizontalsystems.marketkit.models.Token

class ManageWalletsFragment : BaseComposeFragment() {

    private val vmFactory by lazy { ManageWalletsModule.Factory() }
    private val viewModel by viewModels<ManageWalletsViewModel> { vmFactory }
    private val restoreSettingsViewModel by viewModels<RestoreSettingsViewModel> { vmFactory }

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            NiaBackground {
                ManageWalletsScreen(
                    findNavController(),
                    viewModel,
                    restoreSettingsViewModel
                )
            }
        }
    }

}

@Composable
private fun ManageWalletsScreen(
    navController: NavController,
    viewModel: ManageWalletsViewModel,
    restoreSettingsViewModel: RestoreSettingsViewModel
) {
    val coinItems by viewModel.viewItemsLiveData.observeAsState()

    if (restoreSettingsViewModel.openZcashConfigure != null) {
        restoreSettingsViewModel.zcashConfigureOpened()

        navController.getNavigationResult(ZcashConfigure.resultBundleKey) { bundle ->
            val requestResult = bundle.getInt(ZcashConfigure.requestResultKey)

            if (requestResult == ZcashConfigure.RESULT_OK) {
                val zcashConfig = bundle.parcelable<ZCashConfig>(ZcashConfigure.zcashConfigKey)
                zcashConfig?.let { config ->
                    restoreSettingsViewModel.onEnter(config)
                }
            } else {
                restoreSettingsViewModel.onCancelEnterBirthdayHeight()
            }
        }

        navController.slideFromBottom(R.id.zcashConfigure)
    }

    SearchBar(
        title = stringResource(R.string.ManageCoins_title),
        onSearchTextChanged = {
            viewModel.updateFilter(it)
        },
        hint = stringResource(R.string.ManageCoins_Search),
        navigationAction = {
            navController.popBackStack()
        },
        menuItems = listOf(
            MenuItem(
                title = TranslatableString.ResString(R.string.ManageCoins_AddToken),
                icon = R.drawable.ic_add_yellow,
                onClick = {
                    navController.slideFromRight(R.id.addTokenFragment)
                },
                tint = MaterialTheme.colorScheme.onSurface
            )
        ),
        content = {
            Content(coinItems, viewModel, navController)
        }
    )
}

@Composable
private fun Content(
    coinItems: List<CoinViewItem<Token>>?,
    viewModel: ManageWalletsViewModel,
    navController: NavController
) {
    coinItems?.let { items ->
        if (items.isEmpty()) {
            ListEmptyView(
                text = stringResource(R.string.ManageCoins_NoResults),
                icon = R.drawable.ic_not_found
            )
        } else {
            LazyColumn {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = ComposeAppTheme.colors.steel10,
                    )
                }
                items(
                    items = items,
                    key = { it.item })
                { viewItem ->
                    CoinCell(
                        viewItem = viewItem,
                        onItemClick = {
                            if (viewItem.enabled) {
                                viewModel.disable(viewItem.item)
                            } else {
                                viewModel.enable(viewItem.item)
                            }
                        },
                        onInfoClick = {
                            navController.slideFromBottom(
                                R.id.configuredTokenInfo,
                                ConfiguredTokenInfoDialog.prepareParams(viewItem.item)
                            )
                        }
                    )
                }
                item {
                    VSpacer(height = 32.dp)
                }
            }
        }
    }
}


@Composable
private fun CoinCell(
    viewItem: CoinViewItem<Token>,
    onItemClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Column {
        RowUniversal(
            onClick = onItemClick,
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalPadding = 0.dp
        ) {
            Image(
                painter = viewItem.imageSource.painter(),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp, top = 12.dp, bottom = 12.dp)
                    .size(32.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    body_leah(
                        text = viewItem.title,
                        maxLines = 1,
                    )
                    viewItem.label?.let { labelText ->
                        Box(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ComposeAppTheme.colors.jeremy)
                        ) {
                            Text(
                                modifier = Modifier.padding(
                                    start = 4.dp,
                                    end = 4.dp,
                                    bottom = 1.dp
                                ),
                                text = labelText,
                                color = ComposeAppTheme.colors.bran,
                                style = ComposeAppTheme.typography.microSB,
                                maxLines = 1,
                            )
                        }
                    }
                }
                subhead2_grey(
                    text = viewItem.subtitle,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            if (viewItem.hasInfo) {
                HsIconButton(onClick = onInfoClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_info_20),
                        contentDescription = null,
                        tint = ComposeAppTheme.colors.grey
                    )
                }
            }
            HsSwitch(
                modifier = Modifier.padding(0.dp),
                checked = viewItem.enabled,
                onCheckedChange = { onItemClick.invoke() },
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = ComposeAppTheme.colors.steel10,
        )
    }
}

//@Preview
//@Composable
//fun PreviewCoinCell() {
//    val viewItem = CoinViewItem(
//        item = "ethereum",
//        imageSource = ImageSource.Local(R.drawable.logo_ethereum_24),
//        title = "ETH",
//        subtitle = "Ethereum",
//        enabled = true,
//        hasSettings = true,
//        hasInfo = true,
//        label = "Ethereum"
//    )
//    ComposeAppTheme {
//        CoinCell(
//            viewItem,
//            {},
//            {},
//            {}
//        )
//    }
//}
