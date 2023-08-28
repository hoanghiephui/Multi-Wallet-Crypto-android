package io.horizontalsystems.bankwallet.modules.evmnetwork.addrpc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coin.chain.crypto.core.designsystem.component.TopAppBarClose
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.entities.DataState
import io.horizontalsystems.bankwallet.modules.evmfee.ButtonsGroupWithShade
import io.horizontalsystems.bankwallet.modules.swap.settings.Caution
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRpcScreen(
    navController: NavController,
    viewModel: AddRpcViewModel = hiltViewModel()
) {
    if (viewModel.viewState.closeScreen) {
        navController.popBackStack()
        viewModel.onScreenClose()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBarClose(
            titleRes = R.string.AddEvmSyncSource_AddRPCSource,
            actionIcon = Icons.Rounded.Close,
            actionIconContentDescription = "ArrowBack",
            onActionClick = { navController.popBackStack() },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            HeaderText(stringResource(id = R.string.AddEvmSyncSource_RpcUrl))
            FormsInput(
                modifier = Modifier.padding(horizontal = 16.dp),
                qrScannerEnabled = true,
                onValueChange = viewModel::onEnterRpcUrl,
                hint = "",
                state = getState(viewModel.viewState.urlCaution),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(24.dp))

            HeaderText(stringResource(id = R.string.AddEvmSyncSource_BasicAuthentication))
            FormsInput(
                modifier = Modifier.padding(horizontal = 16.dp),
                qrScannerEnabled = true,
                onValueChange = viewModel::onEnterBasicAuth,
                hint = "",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Spacer(Modifier.height(60.dp))
        }

        ButtonPrimaryYellow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            title = stringResource(R.string.Button_Add),
            onClick = { viewModel.onAddClick() },
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

private fun getState(caution: Caution?) = when (caution?.type) {
    Caution.Type.Error -> DataState.Error(Exception(caution.text))
    Caution.Type.Warning -> DataState.Error(FormsInputStateWarning(caution.text))
    null -> null
}