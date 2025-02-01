package io.horizontalsystems.bankwallet.modules.settings.main

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.analytics.TrackScreenViewEvent
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.core.stats.StatEvent
import io.horizontalsystems.bankwallet.core.stats.StatPage
import io.horizontalsystems.bankwallet.core.stats.stat
import io.horizontalsystems.bankwallet.modules.billing.showBillingPlusDialog
import io.horizontalsystems.bankwallet.modules.contacts.ContactsFragment
import io.horizontalsystems.bankwallet.modules.contacts.Mode
import io.horizontalsystems.bankwallet.modules.manageaccount.dialogs.BackupRequiredDialog
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.bankwallet.modules.settings.vipsupport.VipSupportBottomSheet
import io.horizontalsystems.bankwallet.modules.walletconnect.WCAccountTypeNotSupportedDialog
import io.horizontalsystems.bankwallet.modules.walletconnect.WCManager
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.BadgeText
import io.horizontalsystems.bankwallet.ui.compose.components.CellSingleLineLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.PremiumHeader
import io.horizontalsystems.bankwallet.ui.compose.components.RowUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah
import io.horizontalsystems.bankwallet.ui.compose.components.caption_grey
import io.horizontalsystems.bankwallet.ui.compose.components.cell.SectionPremiumUniversalLawrence
import io.horizontalsystems.bankwallet.ui.compose.components.subhead1_grey
import io.horizontalsystems.bankwallet.ui.helpers.LinkHelper
import se.warting.inappupdate.compose.findActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: MainSettingsViewModel = viewModel(factory = MainSettingsModule.Factory()),
) {
    var isVipSupportVisible by remember { mutableStateOf(false) }
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppBar(
                title = stringResource(R.string.Settings_Title),
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(it)) {
            Spacer(modifier = Modifier.height(12.dp))
            SettingSections(
                    viewModel = viewModel,
                    navController = navController,
                    openVipSupport = {
                        isVipSupportVisible = true
                    })
            //SettingsFooter(viewModel.appVersion, viewModel.companyWebPage)
        }
        VipSupportBottomSheet(
            isBottomSheetVisible = isVipSupportVisible,
            close = { isVipSupportVisible = false }
        )
    }
    TrackScreenViewEvent("SettingsScreen")
}

@Composable
private fun SettingSections(
    viewModel: MainSettingsViewModel,
    navController: NavController,
    openVipSupport: () -> Unit
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    CellUniversalLawrenceSection(
        listOf {
            HsSettingCell(
                R.string.billing_plus_title,
                R.drawable.ic_heart_filled_24,
                ComposeAppTheme.colors.jacob,
                onClick = {
                    context.findActivity().showBillingPlusDialog()

                    stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.Donate))
                }
            )
        }
    )
    if (uiState.showPremiumBanner) {
        PremiumBanner(
            onClick = {
                navController.slideFromBottom(R.id.buySubscriptionFragment)
            }
        )
        VSpacer(20.dp)
    }

    VSpacer(12.dp)

    CellUniversalLawrenceSection(
        listOf {
            HsSettingCell(
                R.string.Settings_GetYourTokens,
                R.drawable.ic_uwt2_24,
                ComposeAppTheme.colors.jacob,
                onClick = {
                    LinkHelper.openLinkInAppBrowser(context, "https://t.me/BeUnstoppable_bot")
                }
            )
        }
    )

    VSpacer(32.dp)

    CellUniversalLawrenceSection(
        listOf({
            HsSettingCell(
                R.string.SettingsSecurity_ManageKeys,
                R.drawable.ic_wallet_20,
                showAlert = uiState.manageWalletShowAlert,
                onClick = {
                    navController.slideFromRight(
                        R.id.manageAccountsFragment,
                        ManageAccountsModule.Mode.Manage
                    )

                    stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.ManageWallets))
                }
            )
        }, {
            HsSettingCell(
                R.string.BlockchainSettings_Title,
                R.drawable.ic_blocks_20,
                onClick = {
                    navController.slideFromRight(R.id.blockchainSettingsFragment)

                    stat(
                        page = StatPage.Settings,
                        event = StatEvent.Open(StatPage.BlockchainSettings)
                    )
                }
            )
        }, {
            HsSettingCell(
                R.string.Settings_WalletConnect,
                R.drawable.ic_wallet_connect_20,
                value = (uiState.wcCounterType as? MainSettingsModule.CounterType.SessionCounter)?.number?.toString(),
                counterBadge = (uiState.wcCounterType as? MainSettingsModule.CounterType.PendingRequestCounter)?.number?.toString(),
                onClick = {
                    when (val state = viewModel.walletConnectSupportState) {
                        WCManager.SupportState.Supported -> {
                            navController.slideFromRight(R.id.wcListFragment)

                            stat(
                                page = StatPage.Settings,
                                event = StatEvent.Open(StatPage.WalletConnect)
                            )
                        }

                        WCManager.SupportState.NotSupportedDueToNoActiveAccount -> {
                            navController.slideFromBottom(R.id.wcErrorNoAccountFragment)
                        }

                        is WCManager.SupportState.NotSupportedDueToNonBackedUpAccount -> {
                            val text = Translator.getString(R.string.WalletConnect_Error_NeedBackup)
                            navController.slideFromBottom(
                                R.id.backupRequiredDialog,
                                BackupRequiredDialog.Input(state.account, text)
                            )

                            stat(
                                page = StatPage.Settings,
                                event = StatEvent.Open(StatPage.BackupRequired)
                            )
                        }

                        is WCManager.SupportState.NotSupported -> {
                            navController.slideFromBottom(
                                R.id.wcAccountTypeNotSupportedDialog,
                                WCAccountTypeNotSupportedDialog.Input(state.accountTypeDescription)
                            )
                        }
                    }
                }
            )
        }, {
            HsSettingCell(
                title = R.string.Settings_TonConnect,
                icon = R.drawable.ic_ton_connect_24,
                value = null,
                counterBadge = null,
                onClick = {
                    navController.slideFromRight(R.id.tcListFragment)

                    stat(
                        page = StatPage.Settings,
                        event = StatEvent.Open(StatPage.TonConnect)
                    )
                }
            )
        }, {
            HsSettingCell(
                R.string.BackupManager_Title,
                R.drawable.ic_file_24,
                onClick = {
                    navController.slideFromRight(R.id.backupManagerFragment)

                    stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.BackupManager))
                }
            )
        }
        )
    )

    VSpacer(32.dp)

    CellUniversalLawrenceSection(
        listOf(
            {
                HsSettingCell(
                    R.string.Settings_SecurityCenter,
                    R.drawable.ic_security,
                    showAlert = uiState.securityCenterShowAlert,
                    onClick = {
                        navController.slideFromRight(R.id.securitySettingsFragment)

                        stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.Security))
                    }
                )
            },
            {
                HsSettingCell(
                    R.string.Settings_Privacy,
                    R.drawable.ic_eye_20,
                    onClick = {
                        navController.slideFromRight(R.id.privacySettingsFragment)

                        stat(page = StatPage.AboutApp, event = StatEvent.Open(StatPage.Privacy))
                    }
                )
            },
            {
                HsSettingCell(
                    R.string.Contacts,
                    R.drawable.ic_user_20,
                    onClick = {
                        navController.slideFromRight(
                            R.id.contactsFragment,
                            ContactsFragment.Input(Mode.Full)
                        )

                        stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.Contacts))
                    }
                )
            },
            {
                HsSettingCell(
                    R.string.Settings_Appearance,
                    R.drawable.ic_brush_20,
                    onClick = {
                        navController.slideFromRight(R.id.appearanceFragment)

                        stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.Appearance))
                    }
                )
            },
            {
                HsSettingCell(
                    R.string.Settings_Subscription,
                    R.drawable.ic_star_24,
                    value = if (uiState.hasSubscription) stringResource(R.string.SettingsSubscription_Active) else null,
                    onClick = {
                        navController.slideFromRight(R.id.subscriptionFragment)
                    }
                )
            },
            {
                HsSettingCell(
                    R.string.Settings_BaseCurrency,
                    R.drawable.ic_currency,
                    value = uiState.baseCurrencyCode,
                    onClick = {
                        navController.slideFromRight(R.id.baseCurrencySettingsFragment)

                        stat(
                            page = StatPage.Settings,
                            event = StatEvent.Open(StatPage.BaseCurrency)
                        )
                    }
                )
            },
            {
                HsSettingCell(
                    R.string.Settings_Language,
                    R.drawable.ic_language,
                    value = uiState.currentLanguage,
                    onClick = {
                        navController.slideFromRight(R.id.languageSettingsFragment)

                        stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.Language))
                    }
                )
            }
        )
    )

    VSpacer(32.dp)

    CellUniversalLawrenceSection(
        listOf(
            {
                HsSettingCell(
                    R.string.Settings_BotSupport,
                    R.drawable.ic_ai_assistant_24,
                    onClick = {

                    }
                )
            },
        )
    )

    VSpacer(24.dp)

    PremiumHeader()

    SectionPremiumUniversalLawrence {
        HsSettingCell(
            title = R.string.Settings_VipSupport,
            icon = R.drawable.ic_support_yellow_24,
            iconTint = ComposeAppTheme.colors.jacob,
            onClick = openVipSupport
        )
    }

    VSpacer(32.dp)

    CellUniversalLawrenceSection(
        listOf({
            HsSettingCell(
                R.string.SettingsAboutApp_Title,
                R.drawable.ic_about_app_20,
                showAlert = uiState.aboutAppShowAlert,
                onClick = {
                    navController.slideFromRight(R.id.aboutAppFragment)

                    stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.AboutApp))
                }
            )
        }, {
            HsSettingCell(
                R.string.Settings_ShareThisWallet,
                R.drawable.ic_share_20,
                onClick = {
                    shareAppLink(uiState.appWebPageLink, context)

                    stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.TellFriends))
                }
            )
        })
    )

    VSpacer(24.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        subhead1_grey(text = stringResource(id = R.string.Settings_JoinUnstoppables).uppercase())
    }
    CellUniversalLawrenceSection(
        listOf({
            HsSettingCell(
                R.string.Settings_Telegram,
                R.drawable.ic_telegram_24,
                onClick = {
                    LinkHelper.openLinkInAppBrowser(context, App.appConfigProvider.appTelegramLink)

                    stat(
                        page = StatPage.Settings,
                        event = StatEvent.Open(StatPage.ExternalTelegram)
                    )
                }
            )
        }, {
            HsSettingCell(
                R.string.Settings_Twitter,
                R.drawable.ic_twitter_24,
                onClick = {
                    LinkHelper.openLinkInAppBrowser(context, App.appConfigProvider.appTwitterLink)

                    stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.ExternalTwitter))
                }
            )
        })
    )

    VSpacer(32.dp)

    CellUniversalLawrenceSection(
        listOf {
            HsSettingCell(
                R.string.Settings_Donate,
                R.drawable.ic_heart_24,
                onClick = {
                    navController.slideFromRight(R.id.donateTokenSelectFragment)

                    stat(page = StatPage.Settings, event = StatEvent.Open(StatPage.Donate))
                }
            )
        }
    )

    VSpacer(32.dp)
}

@Composable
fun HsSettingCell(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    iconTint: Color? = null,
    value: String? = null,
    counterBadge: String? = null,
    showAlert: Boolean = false,
    onClick: () -> Unit
) {
    RowUniversal(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconTint ?: ComposeAppTheme.colors.grey
        )
        body_leah(
            text = stringResource(title),
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.weight(1f))

        if (counterBadge != null) {
            BadgeText(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = counterBadge
            )
        } else if (value != null) {
            subhead1_grey(
                text = value,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        if (showAlert) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_attention_red_20),
                contentDescription = null,
            )
            Spacer(Modifier.width(12.dp))
        }
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
        )
    }
}

@Composable
private fun SettingsFooter(appVersion: String, companyWebPage: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        caption_grey(
            text = stringResource(
                R.string.Settings_InfoTitleWithVersion,
                appVersion
            ).uppercase()
        )
        HorizontalDivider(
            modifier = Modifier
                .width(100.dp)
                .padding(top = 8.dp, bottom = 4.5.dp),
            thickness = 0.5.dp,
            color = ComposeAppTheme.colors.steel20
        )
        Text(
            text = stringResource(R.string.Settings_InfoSubtitle),
            style = ComposeAppTheme.typography.micro,
            color = ComposeAppTheme.colors.grey,
        )
        Image(
            modifier = Modifier
                .padding(top = 32.dp)
                .size(32.dp)
                .clickable {
                    LinkHelper.openLinkInAppBrowser(context, companyWebPage)
                },
            painter = painterResource(id = R.drawable.ic_company_logo),
            contentDescription = null,
        )
        caption_grey(
            modifier = Modifier.padding(top = 12.dp, bottom = 32.dp),
            text = stringResource(R.string.Settings_CompanyName),
        )
    }
}

private fun shareAppLink(appLink: String, context: Context) {
    val shareMessage = Translator.getString(R.string.SettingsShare_Text) + "\n" + appLink + "\n"
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
    context.startActivity(
        Intent.createChooser(
            shareIntent,
            Translator.getString(R.string.SettingsShare_Title)
        )
    )
}

@Preview
@Composable
private fun PreviewSettingsScreen() {
    ComposeAppTheme {
        Column {
            CellSingleLineLawrenceSection(
                listOf({
                    HsSettingCell(
                        R.string.Settings_Faq,
                        R.drawable.ic_faq_20,
                        showAlert = true,
                        onClick = { }
                    )
                }, {
                    HsSettingCell(
                        R.string.Guides_Title,
                        R.drawable.ic_academy_20,
                        onClick = { }
                    )
                })
            )

            Spacer(Modifier.height(32.dp))

            CellSingleLineLawrenceSection(
                listOf {
                    HsSettingCell(
                        R.string.Settings_WalletConnect,
                        R.drawable.ic_wallet_connect_20,
                        counterBadge = "13",
                        onClick = { }
                    )
                }
            )
        }
    }
}
