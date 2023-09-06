package io.horizontalsystems.bankwallet.modules.manageaccounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coin.chain.crypto.core.designsystem.component.TopAppBar
import coin.chain.crypto.core.designsystem.theme.NiaTheme
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.navigateWithTermsAccepted
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.backupalert.BackupAlert
import io.horizontalsystems.bankwallet.modules.manageaccount.ManageAccountModule
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsModule.AccountViewItem
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsModule.ActionViewItem
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.RedL
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonSecondaryCircle
import io.horizontalsystems.bankwallet.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.RowUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.body_jacob
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah
import io.horizontalsystems.bankwallet.ui.compose.components.subhead2_grey
import io.horizontalsystems.bankwallet.ui.compose.components.subhead2_lucian

class ManageAccountsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        /*ManageAccountsScreen(
            findNavController(),
            arguments?.parcelable(ManageAccountsModule.MODE)!!
        )*/
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAccountsScreen(navController: NavController, viewModel: ManageAccountsViewModel) {
    BackupAlert(navController)


    val viewItems = viewModel.viewItems
    val finish = viewModel.finish
    val isCloseButtonVisible = viewModel.isCloseButtonVisible

    if (finish) {
        navController.popBackStack()
    }

    NiaTheme {
        Column {

            TopAppBar(
                titleRes = R.string.ManageAccounts_Title,
                navigationIcon = Icons.Rounded.ArrowBack,
                navigationIconContentDescription = "ArrowBack",
                onNavigationClick = {
                    navController.popBackStack()
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                )
            )

            LazyColumn(modifier = Modifier.background(color = Color.Transparent)) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    viewItems?.let { (regularAccounts, watchAccounts) ->
                        if (regularAccounts.isNotEmpty()) {
                            AccountsSection(regularAccounts, viewModel, navController)
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        if (watchAccounts.isNotEmpty()) {
                            AccountsSection(watchAccounts, viewModel, navController)
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    val args = when (viewModel.mode) {
                        ManageAccountsModule.Mode.Manage -> ManageAccountsModule.prepareParams(
                            R.id.manageAccountsFragment,
                            false
                        )

                        ManageAccountsModule.Mode.Switcher -> ManageAccountsModule.prepareParams(
                            R.id.manageAccountsFragment,
                            true
                        )
                    }

                    val actions = listOf(
                        ActionViewItem(
                            R.drawable.ic_plus,
                            R.string.ManageAccounts_CreateNewWallet
                        ) {
                            navController.navigateWithTermsAccepted {
                                navController.slideFromRight(R.id.createAccountFragment, args)
                            }
                        },
                        ActionViewItem(
                            R.drawable.ic_download_20,
                            R.string.ManageAccounts_ImportWallet
                        ) {
                            navController.slideFromBottom(R.id.importWalletFragment, args)
                        },
                        ActionViewItem(
                            R.drawable.icon_binocule_20,
                            R.string.ManageAccounts_WatchAddress
                        ) {
                            navController.slideFromRight(R.id.watchAddressFragment, args)
                        }
                    )
                    CellUniversalLawrenceSection(actions) {
                        RowUniversal(
                            onClick = it.callback
                        ) {
                            Icon(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                painter = painterResource(id = it.icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            body_jacob(text = stringResource(id = it.title))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun AccountsSection(
    accounts: List<AccountViewItem>,
    viewModel: ManageAccountsViewModel,
    navController: NavController
) {
    CellUniversalLawrenceSection(items = accounts) { accountViewItem ->
        RowUniversal(
            onClick = { viewModel.onSelect(accountViewItem) }
        ) {
            if (accountViewItem.selected) {
                Icon(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    painter = painterResource(id = R.drawable.ic_radion),
                    contentDescription = null,
                    tint = ComposeAppTheme.colors.jacob
                )
            } else {
                Icon(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    painter = painterResource(id = R.drawable.ic_radioff),
                    contentDescription = null,
                    tint = ComposeAppTheme.colors.grey
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                body_leah(text = accountViewItem.title)
                if (accountViewItem.backupRequired) {
                    subhead2_lucian(text = stringResource(id = R.string.ManageAccount_BackupRequired_Title))
                } else if (accountViewItem.migrationRequired) {
                    subhead2_lucian(text = stringResource(id = R.string.ManageAccount_MigrationRequired_Title))
                } else {
                    subhead2_grey(
                        text = accountViewItem.subtitle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            if (accountViewItem.isWatchAccount) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_binocule_20),
                    contentDescription = null,
                    tint = ComposeAppTheme.colors.grey
                )
            }

            val icon: Int
            val iconTint: Color
            if (accountViewItem.showAlertIcon) {
                icon = R.drawable.icon_warning_2_20
                iconTint = RedL
            } else {
                icon = R.drawable.ic_more2_20
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant
            }

            ButtonSecondaryCircle(
                modifier = Modifier.padding(horizontal = 16.dp),
                icon = icon,
                tint = iconTint
            ) {
                navController.slideFromRight(
                    R.id.manageAccountFragment,
                    ManageAccountModule.prepareParams(accountViewItem.accountId)
                )
            }
        }
    }
}
