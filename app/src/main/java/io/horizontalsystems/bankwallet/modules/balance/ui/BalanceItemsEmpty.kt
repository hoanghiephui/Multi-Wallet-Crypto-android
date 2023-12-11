package io.horizontalsystems.bankwallet.modules.balance.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.AdType
import io.horizontalsystems.bankwallet.core.AdViewState
import io.horizontalsystems.bankwallet.core.MaxTemplateNativeAdViewComposable
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.balance.AccountViewItem
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonPrimaryYellow
import io.horizontalsystems.bankwallet.ui.compose.components.ScreenMessageWithAction

@Composable
fun BalanceItemsEmpty(
    navController: NavController,
    accountViewItem: AccountViewItem,
    nativeAd: AdViewState
) {
    if (accountViewItem.isWatchAccount) {
        ScreenMessageWithAction(
            text = stringResource(R.string.Balance_WatchAccount_NoBalance),
            icon = R.drawable.ic_empty_wallet
        ) {
            MaxTemplateNativeAdViewComposable(nativeAd, AdType.SMALL)
        }
    } else {
        ScreenMessageWithAction(
            text = stringResource(R.string.Balance_NoCoinsAlert),
            icon = R.drawable.ic_add_to_wallet_2_48
        ) {
            MaxTemplateNativeAdViewComposable(nativeAd, AdType.SMALL)
            Spacer(modifier = Modifier.height(16.dp))
            ButtonPrimaryYellow(
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth(),
                title = stringResource(R.string.Balance_AddCoins),
                onClick = { navController.slideFromRight(R.id.manageWalletsFragment) }
            )
        }
    }
}
