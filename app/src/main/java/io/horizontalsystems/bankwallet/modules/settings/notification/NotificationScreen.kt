package io.horizontalsystems.bankwallet.modules.settings.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.modules.settings.security.SecurityCenterCell
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.HsSwitch
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah


class SettingNotificationScreen : BaseComposeFragment() {
    private val viewModel by viewModels<SettingNotificationViewModel> {
        Factory()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun GetContent(navController: NavController) {
        val uiState = viewModel.uiState
        val context = LocalContext.current
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppBar(
                    title = stringResource(R.string.Notification_Title),
                    navigationIcon = {
                        HsBackButton(onClick = { navController.popBackStack() })
                    },
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                CellUniversalLawrenceSection {
                    SecurityCenterCell(
                        start = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chart_24),
                                tint = ComposeAppTheme.colors.grey,
                                modifier = Modifier.size(24.dp),
                                contentDescription = null
                            )
                        },
                        center = {
                            body_leah(
                                text = "Real-time crypto prices",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        end = {
                            HsSwitch(
                                checked = uiState.isShowNotificationPrice,
                                onCheckedChange = {
                                    viewModel.onSetNotificationPrice(it, context)
                                }
                            )
                        }
                    )
                }

                VSpacer(height = 16.dp)

                CellUniversalLawrenceSection {
                    SecurityCenterCell(
                        start = {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_newspaper_24),
                                tint = ComposeAppTheme.colors.grey,
                                modifier = Modifier.size(24.dp),
                                contentDescription = null
                            )
                        },
                        center = {
                            body_leah(
                                text = "The latest cryptocurrency news",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        end = {
                            HsSwitch(
                                checked = uiState.isShowNotificationNews,
                                onCheckedChange = {
                                    viewModel.onSetNotificationNews(it, context)
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    override val logScreen: String
        get() = "SettingNotificationScreen"
}