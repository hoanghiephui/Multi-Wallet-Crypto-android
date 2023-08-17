package io.horizontalsystems.bankwallet.modules.tor

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonSecondaryTransparent
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.coroutines.delay

@Composable
fun TorStatusView(
    viewModel: TorConnectionViewModel = viewModel(factory = TorConnectionModule.Factory())
) {

    val animatedSize by animateDpAsState(
        targetValue = if (viewModel.torViewState.torIsActive) 20.dp else 50.dp,
        animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing), label = ""
    )

    Divider()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(animatedSize),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.torViewState.torIsActive) {
            val startColor = MaterialTheme.colorScheme.primary
            val endColor = MaterialTheme.colorScheme.onPrimary
            val color = remember { Animatable(startColor) }
            val startTextColor = Color.White
            val endTextColor = MaterialTheme.colorScheme.onSurface
            val textColor = remember { Animatable(startTextColor) }
            LaunchedEffect(Unit) {
                delay(1000)
                color.animateTo(endColor, animationSpec = tween(250, easing = LinearEasing))
                textColor.animateTo(endTextColor, animationSpec = tween(250, easing = LinearEasing))
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxSize()
                    .background(color.value),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.Tor_TorIsActive),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.value,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(viewModel.torViewState.stateText),
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (viewModel.torViewState.showRetryButton) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                )
                if (viewModel.torViewState.showRetryButton) {
                    ButtonSecondaryTransparent(
                        title = stringResource(R.string.Button_Retry).toUpperCase(Locale.current),
                        onClick = { viewModel.restartTor() }
                    )
                }
            }
        }
    }

    if (viewModel.torViewState.showNetworkConnectionError) {
        val view = LocalView.current
        HudHelper.showErrorMessage(view, R.string.Hud_Text_NoInternet)
        viewModel.networkErrorShown()
    }
}
