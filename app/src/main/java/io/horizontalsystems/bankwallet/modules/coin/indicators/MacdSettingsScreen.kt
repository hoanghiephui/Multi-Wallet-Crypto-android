package io.horizontalsystems.bankwallet.modules.coin.indicators

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coin.chain.crypto.core.designsystem.component.TopAppBar
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.entities.DataState
import io.horizontalsystems.bankwallet.modules.chart.ChartIndicatorSetting
import io.horizontalsystems.bankwallet.modules.evmfee.ButtonsGroupWithShade
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonPrimaryYellow
import io.horizontalsystems.bankwallet.ui.compose.components.FormsInput
import io.horizontalsystems.bankwallet.ui.compose.components.HeaderText
import io.horizontalsystems.bankwallet.ui.compose.components.InfoText
import io.horizontalsystems.bankwallet.ui.compose.components.VSpacer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MacdSettingsScreen(navController: NavController, indicatorSetting: ChartIndicatorSetting) {
    val viewModel = viewModel<MacdSettingViewModel>(
        factory = MacdSettingViewModel.Factory(indicatorSetting)
    )
    val uiState = viewModel.uiState

    if (uiState.finish) {
        LaunchedEffect(uiState.finish) {
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                titleRes = viewModel.name,
                navigationIcon = Icons.Rounded.ArrowBack,
                actionIconContentDescription = "ArrowBack",
                onNavigationClick = { navController.popBackStack() },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
                actionIcon = Icons.Rounded.Restore,
                onActionClick = {
                    viewModel.reset()
                },
                modifier = Modifier,
                enabled = uiState.resetEnabled
            )
        }
    ) {
        Column(Modifier.padding(it).consumeWindowInsets(it).imePadding()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoText(
                    text = stringResource(R.string.CoinPage_MacdSettingsDescription)
                )
                VSpacer(12.dp)
                HeaderText(
                    text = stringResource(R.string.CoinPage_FastLength).uppercase()
                )
                FormsInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    hint = viewModel.defaultFast ?: "",
                    initial = uiState.fast,
                    state = uiState.fastError?.let {
                        DataState.Error(it)
                    },
                    pasteEnabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    onValueChange = {
                        viewModel.onEnterFast(it)
                    }
                )
                VSpacer(24.dp)
                HeaderText(
                    text = stringResource(R.string.CoinPage_SlowLength).uppercase()
                )
                FormsInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    hint = viewModel.defaultSlow ?: "",
                    initial = uiState.slow,
                    state = uiState.slowError?.let {
                        DataState.Error(it)
                    },
                    pasteEnabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    onValueChange = {
                        viewModel.onEnterSlow(it)
                    }
                )
                VSpacer(24.dp)
                HeaderText(
                    text = stringResource(R.string.CoinPage_SignalSmoothing).uppercase()
                )
                FormsInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    hint = viewModel.defaultSignal ?: "",
                    initial = uiState.signal,
                    state = uiState.signalError?.let {
                        DataState.Error(it)
                    },
                    pasteEnabled = false,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    onValueChange = {
                        viewModel.onEnterSignal(it)
                    }
                )
                VSpacer(32.dp)
            }
            ButtonPrimaryYellow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                title = stringResource(R.string.SwapSettings_Apply),
                onClick = {
                    viewModel.save()
                },
                enabled = uiState.applyEnabled
            )
        }
    }
}
