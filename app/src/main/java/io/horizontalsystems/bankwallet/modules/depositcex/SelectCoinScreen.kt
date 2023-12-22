package io.horizontalsystems.bankwallet.modules.depositcex

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.providers.CexAsset
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Loading
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SelectCoinScreen(
    onClose: () -> Unit,
    itemIsSuspended: (DepositCexModule.CexCoinViewItem) -> Boolean,
    onSelectAsset: (CexAsset) -> Unit,
    withBalance: Boolean
) {
    val viewModel =
        viewModel<SelectCexAssetViewModel>(factory = SelectCexAssetViewModel.Factory(withBalance))

    val uiState = viewModel.uiState

    NiaBackground {
        SearchBar(
            title = stringResource(R.string.Cex_ChooseCoin),
            onSearchTextChanged = {
                viewModel.onEnterQuery(it)
            },
            hint = stringResource(R.string.Cex_SelectCoin_Search),
            navigationAction = onClose,
            content = {
                Crossfade(targetState = uiState.loading, label = "") { loading ->
                    if (loading) {
                        Loading()
                    } else {
                        uiState.items?.let { viewItems ->
                            if (viewItems.isEmpty()) {
                                ListEmptyView(
                                    text = stringResource(R.string.EmptyResults),
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
                                    items(viewItems) { viewItem: DepositCexModule.CexCoinViewItem ->
                                        CoinCell(
                                            viewItem = viewItem,
                                            suspended = itemIsSuspended.invoke(viewItem),
                                            onItemClick = {
                                                onSelectAsset.invoke(viewItem.cexAsset)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun CoinCell(
    viewItem: DepositCexModule.CexCoinViewItem,
    suspended: Boolean,
    onItemClick: () -> Unit,
) {
    Column {
        RowUniversal(
            onClick = if (suspended) null else onItemClick,
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalPadding = 0.dp
        ) {
            CoinImage(
                iconUrl = viewItem.coinIconUrl,
                placeholder = viewItem.coinIconPlaceholder,
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
                }
                subhead2_grey(
                    text = viewItem.subtitle,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            if (suspended) {
                HSpacer(width = 16.dp)
                Badge(text = stringResource(R.string.Suspended))
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = ComposeAppTheme.colors.steel10,
        )
    }
}
