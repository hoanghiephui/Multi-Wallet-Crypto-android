package io.horizontalsystems.bankwallet.modules.btcblockchainsettings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coin.chain.crypto.core.designsystem.component.TopAppBar
import coin.chain.crypto.core.designsystem.theme.NiaTheme
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseFragment
import io.horizontalsystems.bankwallet.material.module.info.navigateToBtcBlockchainRestoreSourceInfo
import io.horizontalsystems.bankwallet.material.module.setting.navigations.blockchainSettingsRoute
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonPrimaryYellow
import io.horizontalsystems.bankwallet.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.HeaderText
import io.horizontalsystems.bankwallet.ui.compose.components.InfoText
import io.horizontalsystems.bankwallet.ui.compose.components.RowUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.TextImportantWarning
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah
import io.horizontalsystems.bankwallet.ui.compose.components.subhead2_grey

class BtcBlockchainSettingsFragment : BaseFragment() {

    /*private val viewModel by viewModels<BtcBlockchainSettingsViewModel> {
        BtcBlockchainSettingsModule.Factory(requireArguments())
    }

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
                    BtcBlockchainSettingsScreen(
                        viewModel,
                        findNavController()
                    )
                }
            }
        }
    }*/

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BtcBlockchainSettingsScreen(
    viewModel: BtcBlockchainSettingsViewModel,
    navController: NavController
) {

    if (viewModel.closeScreen) {
        navController.popBackStack(route = blockchainSettingsRoute, inclusive = false)
    }

    NiaTheme {
        Column {
            TopAppBar(
                titleRes = viewModel.title,
                navigationIcon = {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = viewModel.blockchainIconUrl,
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
                    navController.popBackStack()
                },
                actionIconContentDescription = "Button_Close"
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(12.dp))

                TextImportantWarning(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = stringResource(R.string.BtcBlockchainSettings_RestoreSourceChangeWarning)
                )

                Spacer(Modifier.height(24.dp))
                RestoreSourceSettings(viewModel, navController)
                Spacer(Modifier.height(32.dp))
            }
            if (viewModel.saveButtonEnabled) {
                ButtonPrimaryYellow(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    title = stringResource(R.string.Button_Save),
                    enabled = viewModel.saveButtonEnabled,
                    onClick = { viewModel.onSaveClick() }
                )
                Spacer(Modifier.height(16.dp))
            }
        }

    }
}

@Composable
private fun RestoreSourceSettings(
    viewModel: BtcBlockchainSettingsViewModel,
    navController: NavController
) {
    BlockchainSettingSection(
        viewModel.restoreSources,
        R.string.BtcBlockchainSettings_RestoreSource,
        R.string.BtcBlockchainSettings_RestoreSourceSettingsDescription,
        { viewItem -> viewModel.onSelectRestoreMode(viewItem) },
        navController
    )
}

@Composable
private fun BlockchainSettingSection(
    restoreSources: List<BtcBlockchainSettingsModule.ViewItem>,
    settingTitleTextRes: Int,
    settingDescriptionTextRes: Int,
    onItemClick: (BtcBlockchainSettingsModule.ViewItem) -> Unit,
    navController: NavController
) {
    HeaderText(
        text = stringResource(settingTitleTextRes),
        onInfoClick = {
            navController.navigateToBtcBlockchainRestoreSourceInfo()
        })
    CellUniversalLawrenceSection(restoreSources) { item ->
        BlockchainSettingCell(item.title, item.subtitle, item.selected) {
            onItemClick(item)
        }
    }
    InfoText(
        text = stringResource(settingDescriptionTextRes),
    )
}

@Composable
fun BlockchainSettingCell(
    title: String,
    subtitle: String,
    checked: Boolean,
    onClick: () -> Unit
) {
    RowUniversal(
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            body_leah(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(1.dp))
            subhead2_grey(
                text = subtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .width(52.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    painter = painterResource(R.drawable.ic_checkmark_20),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            }
        }
    }
}
