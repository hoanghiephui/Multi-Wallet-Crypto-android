package io.horizontalsystems.bankwallet.modules.settings.subscription

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.modules.billing.PLAY_STORE_SUBSCRIPTION_URL
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.CellUniversalLawrenceSection
import io.horizontalsystems.bankwallet.ui.compose.components.HsBackButton
import io.horizontalsystems.bankwallet.ui.compose.components.InfoText
import io.horizontalsystems.bankwallet.ui.compose.components.RowUniversal
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer
import io.horizontalsystems.bankwallet.ui.compose.components.body_leah
import io.horizontalsystems.bankwallet.ui.compose.components.subhead1_jacob

class SubscriptionFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent(navController: NavController) {
        SubscriptionScreen(navController)
    }

    override val logScreen: String
        get() = "SubscriptionFragment"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(navController: NavController) {
    val viewModel = viewModel<SubscriptionViewModel>(factory = SubscriptionModule.Factory())
    val context = LocalContext.current
    val uiState = viewModel.uiState

    Column(
        modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)
    ) {
        AppBar(
            title = stringResource(R.string.Settings_Subscription),
            navigationIcon = {
                HsBackButton(onClick = { navController.popBackStack() })
            }
        )
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            VSpacer(12.dp)
            CellUniversalLawrenceSection {
                RowUniversal(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {
                        navController.slideFromBottom(R.id.buySubscriptionFragment)
                    }
                ) {
                    body_leah(
                        text = stringResource(R.string.SettingsSubscription_SubscriptionPlan),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    uiState.subscriptionName?.let {
                        subhead1_jacob(
                            text = it,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                    )
                }
            }
            InfoText(
                text = stringResource(R.string.SettingsSubscription_SubscriptionInfo, "10.06.25"),
            )

            Row(modifier = Modifier.padding(end = 8.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(PLAY_STORE_SUBSCRIPTION_URL)
                        context.startActivity(intent)
                    } catch (ex: Exception) {
                        Toast.makeText(context, "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = "Open Payment & subscriptions")
                }
            }
        }
    }
}