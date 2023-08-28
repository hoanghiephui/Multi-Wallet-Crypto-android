package io.horizontalsystems.bankwallet.material.module.balance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coin.chain.crypto.ui.rememberLifecycleEvent
import io.horizontalsystems.bankwallet.modules.balance.ui.BalanceScreen
import io.horizontalsystems.bankwallet.modules.keystore.KeyStoreActivity
import io.horizontalsystems.bankwallet.modules.keystore.NoSystemLockWarning
import io.horizontalsystems.bankwallet.modules.launcher.LaunchModule
import io.horizontalsystems.bankwallet.modules.launcher.LaunchViewModel

@Composable
fun BalanceRouter(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val viewModel = viewModel<LaunchViewModel>(factory = LaunchModule.Factory())
    val context = LocalContext.current
    val lifecycleEvent = rememberLifecycleEvent()
    if (lifecycleEvent == Lifecycle.Event.ON_RESUME) {
        when (viewModel.getPage()) {
            LaunchViewModel.Page.Unlock -> {
            }

            LaunchViewModel.Page.NoSystemLock -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    NoSystemLockWarning()
                }
            }

            LaunchViewModel.Page.KeyInvalidated -> {
                KeyStoreActivity.startForInvalidKey(context)
            }

            LaunchViewModel.Page.UserAuthentication -> {
                KeyStoreActivity.startForUserAuthentication(context)
            }

            LaunchViewModel.Page.Main -> {
                BalanceScreen(navController)
            }

            else -> {}
        }
    }
}