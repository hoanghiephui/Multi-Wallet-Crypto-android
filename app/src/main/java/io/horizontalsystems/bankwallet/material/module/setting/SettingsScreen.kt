package io.horizontalsystems.bankwallet.material.module.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import io.horizontalsystems.bankwallet.modules.settings.main.MainSettingsModule
import io.horizontalsystems.bankwallet.modules.settings.main.MainSettingsViewModel
import io.horizontalsystems.bankwallet.modules.settings.main.SettingSections
import io.horizontalsystems.bankwallet.modules.settings.main.SettingsFooter

@Composable
fun SettingsRouter(
    navController: NavHostController,
    viewModel: MainSettingsViewModel = viewModel(factory = MainSettingsModule.Factory()),
) {
    SettingScreen(viewModel = viewModel, navController = navController)
}

@Composable
fun SettingScreen(
    viewModel: MainSettingsViewModel,
    navController: NavController
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(12.dp))
        SettingSections(viewModel, navController)
        SettingsFooter(viewModel.appVersion, viewModel.companyWebPage)
    }
}