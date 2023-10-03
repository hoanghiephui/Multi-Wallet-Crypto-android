package io.horizontalsystems.bankwallet.modules.backuplocal.fullbackup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.evmfee.ButtonsGroupWithShade
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonPrimaryYellow
import io.horizontalsystems.bankwallet.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.HsCheckbox
import io.horizontalsystems.bankwallet.ui.compose.components.RowUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah
import io.horizontalsystems.bankwallet.ui.compose.components.subhead2_grey
import io.horizontalsystems.bankwallet.ui.compose.components.subhead2_lucian

@Composable
fun SelectBackupItemsScreen(
    onNextClick: (accountIds: List<String>) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = viewModel<SelectBackupItemsViewModel>(factory = SelectBackupItemsViewModel.Factory())
    val uiState = viewModel.uiState

    ComposeAppTheme {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                AppBar(
                    title = stringResource(R.string.BackupManager_BаckupFile),
                    navigationIcon = {
                        HsBackButton(onClick = onBackClick)
                    },
                )
            },
            bottomBar = {
                ButtonsGroupWithShade {
                    ButtonPrimaryYellow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        title = stringResource(R.string.Button_Next),
                        onClick = {
                            onNextClick(viewModel.selectedWallets)
                        }
                    )
                }
            }
        ) {
            LazyColumn(modifier = Modifier.padding(it)) {

                when (uiState.viewState) {
                    ViewState.Success -> {
                        item {
                            CellUniversalLawrenceSection(uiState.wallets, showFrame = true) { wallet ->

                                RowUniversal(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = { viewModel.toggle(wallet) }
                                ) {

                                    Column(modifier = Modifier.weight(1f)) {
                                        body_leah(text = wallet.name)
                                        if (wallet.manualBackupRequired) {
                                            subhead2_lucian(text = stringResource(id = R.string.BackupManager_ManualBackupRequired))
                                        } else {
                                            subhead2_grey(
                                                text = wallet.type,
                                                overflow = TextOverflow.Ellipsis,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    HsCheckbox(
                                        checked = wallet.selected,
                                        onCheckedChange = {
                                            viewModel.toggle(wallet)
                                        },
                                    )
                                }
                            }
                        }
                    }

                    is ViewState.Error,
                    ViewState.Loading -> Unit
                }

            }
        }
    }
}