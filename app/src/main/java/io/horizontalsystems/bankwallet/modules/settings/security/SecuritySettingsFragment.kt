package io.horizontalsystems.bankwallet.modules.settings.security

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.modules.main.MainModule
import io.horizontalsystems.bankwallet.modules.settings.security.passcode.SecurityPasscodeSettingsModule
import io.horizontalsystems.bankwallet.modules.settings.security.passcode.SecuritySettingsViewModel
import io.horizontalsystems.bankwallet.modules.settings.security.tor.SecurityTorSettingsModule
import io.horizontalsystems.bankwallet.modules.settings.security.tor.SecurityTorSettingsViewModel
import io.horizontalsystems.bankwallet.modules.settings.security.ui.PasscodeBlock
import io.horizontalsystems.bankwallet.modules.settings.security.ui.TorBlock
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.DisposableLifecycleCallbacks
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.HsSwitch
import io.horizontalsystems.bankwallet.ui.compose.components.InfoText
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground
import io.horizontalsystems.bankwallet.ui.compose.components.RowUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.TextImportantWarning
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah
import io.horizontalsystems.bankwallet.ui.extensions.ConfirmationDialog
import kotlin.system.exitProcess

class SecuritySettingsFragment : BaseComposeFragment() {

    private val torViewModel by viewModels<SecurityTorSettingsViewModel> {
        SecurityTorSettingsModule.Factory()
    }

    private val securitySettingsViewModel by viewModels<SecuritySettingsViewModel> {
        SecurityPasscodeSettingsModule.Factory()
    }

    @Composable
    override fun GetContent(navController: NavController) {
        NiaBackground {
            SecurityCenterScreen(
                securitySettingsViewModel = securitySettingsViewModel,
                torViewModel = torViewModel,
                navController = navController,
                showAppRestartAlert = { showAppRestartAlert() },
                restartApp = { restartApp() },
            )
        }
    }

    override val logScreen: String
        get() = "SecuritySettingsFragment"

    private fun showAppRestartAlert() {
        val warningTitle = if (torViewModel.torCheckEnabled) {
            getString(R.string.Tor_Connection_Enable)
        } else {
            getString(R.string.Tor_Connection_Disable)
        }

        val actionButton = if (torViewModel.torCheckEnabled) {
            getString(R.string.Button_Enable)
        } else {
            getString(R.string.Button_Disable)
        }

        ConfirmationDialog.show(
            icon = R.drawable.ic_tor_connection_24,
            title = getString(R.string.Tor_Alert_Title),
            warningTitle = warningTitle,
            warningText = getString(R.string.SettingsSecurity_AppRestartWarning),
            actionButtonTitle = actionButton,
            transparentButtonTitle = getString(R.string.Alert_Cancel),
            fragmentManager = childFragmentManager,
            listener = object : ConfirmationDialog.Listener {
                override fun onActionButtonClick() {
                    torViewModel.setTorEnabled()
                }

                override fun onTransparentButtonClick() {
                    torViewModel.resetSwitch()
                }

                override fun onCancelButtonClick() {
                    torViewModel.resetSwitch()
                }
            }
        )
    }

    private fun restartApp() {
        activity?.let {
            MainModule.startAsNewTask(it)
            exitProcess(0)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecurityCenterScreen(
    securitySettingsViewModel: SecuritySettingsViewModel,
    torViewModel: SecurityTorSettingsViewModel,
    navController: NavController,
    showAppRestartAlert: () -> Unit,
    restartApp: () -> Unit,
) {

    DisposableLifecycleCallbacks(
        onResume = {
            securitySettingsViewModel.update()
        },
    )

    if (torViewModel.restartApp) {
        restartApp()
        torViewModel.appRestarted()
    }

    val uiState = securitySettingsViewModel.uiState
    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppBar(
                title = stringResource(R.string.Settings_SecurityCenter),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            PasscodeBlock(
                securitySettingsViewModel,
                navController
            )

            VSpacer(height = 32.dp)

            CellUniversalLawrenceSection {
                SecurityCenterCell(
                    start = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_off_24),
                            tint = ComposeAppTheme.colors.grey,
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                    },
                    center = {
                        body_leah(
                            text = stringResource(id = R.string.Appearance_BalanceAutoHide),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    end = {
                        HsSwitch(
                            checked = uiState.balanceAutoHideEnabled,
                            onCheckedChange = {
                                securitySettingsViewModel.onSetBalanceAutoHidden(it)
                            }
                        )
                    }
                )
            }
            InfoText(
                text = stringResource(R.string.Appearance_BalanceAutoHide_Description),
                paddingBottom = 32.dp
            )

            TorBlock(
                torViewModel,
                showAppRestartAlert,
            )

            DuressPasscodeBlock(
                securitySettingsViewModel,
                navController
            )
            InfoText(text = stringResource(R.string.SettingsSecurity_DuressPinDescription))

            VSpacer(height = 32.dp)
            CellUniversalLawrenceSection {
                Column {
                    RowUniversal(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize(),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_analytics_24),
                            tint = ComposeAppTheme.colors.grey,
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(16.dp))
                        body_leah(
                            text = "Analytics logs events for each crash",
                            modifier = Modifier.weight(0.8f),
                            overflow = TextOverflow.Ellipsis,
                        )
                        HsSwitch(
                            checked = uiState.analyticLog,
                            onCheckedChange = {
                                securitySettingsViewModel.onSetAnalytic(it)
                            },
                            modifier = Modifier
                                .weight(0.2f)
                                .padding(end = 8.dp, start = 24.dp)
                        )
                    }
                    HorizontalDivider()
                    RowUniversal(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize(),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_analytics_24),
                            tint = ComposeAppTheme.colors.grey,
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(16.dp))
                        body_leah(
                            text = "Crashlytics collects and analyzes crashes, non-fatal exceptions",
                            modifier = Modifier.weight(0.9f),
                            overflow = TextOverflow.Ellipsis,
                        )
                        HsSwitch(
                            checked = uiState.detectCrash,
                            onCheckedChange = {
                                securitySettingsViewModel.onSetCrashlytics(it)
                            },
                            modifier = Modifier
                                .weight(0.1f)
                                .padding(end = 8.dp)
                        )
                    }
                }

            }
            TextImportantWarning(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = "All data collected anonymously is processed immediately in compliance with the privacy policy, is not shared with third parties, and is only used for the purpose of improving application features."
            )
        }
    }
}

@Composable
fun SecurityCenterCell(
    start: @Composable RowScope.() -> Unit,
    center: @Composable RowScope.() -> Unit,
    end: @Composable (RowScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClick
    ) {
        start.invoke(this)
        Spacer(Modifier.width(16.dp))
        center.invoke(this)
        end?.let {
            Spacer(
                Modifier
                    .defaultMinSize(minWidth = 8.dp)
                    .weight(1f)
            )
            end.invoke(this)
        }
    }
}
