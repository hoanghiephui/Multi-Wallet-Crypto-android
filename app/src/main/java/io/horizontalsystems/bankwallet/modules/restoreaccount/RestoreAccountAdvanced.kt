package io.horizontalsystems.bankwallet.modules.restoreaccount

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.modules.restoreaccount.restoremenu.RestoreMenuModule.RestoreOption
import io.horizontalsystems.bankwallet.modules.restoreaccount.restoremenu.RestoreMenuViewModel
import io.horizontalsystems.bankwallet.modules.restoreaccount.restoremnemonic.RestorePhrase
import io.horizontalsystems.bankwallet.modules.restoreaccount.restoreprivatekey.RestorePrivateKey

@Composable
fun AdvancedRestoreScreen(
    restoreMenuViewModel: RestoreMenuViewModel,
    mainViewModel: RestoreViewModel,
    openSelectCoinsScreen: () -> Unit,
    openNonStandardRestore: () -> Unit,
    onBackClick: () -> Unit,
    fragmentNavController: NavController
) {
    when (restoreMenuViewModel.restoreOption) {
        RestoreOption.RecoveryPhrase -> {
            RestorePhrase(
                advanced = true,
                restoreMenuViewModel = restoreMenuViewModel,
                mainViewModel = mainViewModel,
                openSelectCoins = openSelectCoinsScreen,
                openNonStandardRestore = openNonStandardRestore,
                onBackClick = onBackClick,
                navController = fragmentNavController
            )
        }
        RestoreOption.PrivateKey -> {
            RestorePrivateKey(
                restoreMenuViewModel = restoreMenuViewModel,
                mainViewModel = mainViewModel,
                openSelectCoinsScreen = openSelectCoinsScreen,
                onBackClick = onBackClick,
            )
        }
    }
}
