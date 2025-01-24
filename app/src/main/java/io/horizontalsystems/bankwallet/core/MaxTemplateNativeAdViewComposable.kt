package io.horizontalsystems.bankwallet.core

import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.horizontalsystems.bankwallet.AdNativeUiState
import io.horizontalsystems.bankwallet.core.BaseViewModel.Companion.SHOW_ADS
import io.horizontalsystems.bankwallet.modules.billing.showBillingPlusDialog
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Loading
import io.horizontalsystems.bankwallet.ui.compose.bold
import se.warting.inappupdate.compose.findActivity

/**
 * Jetpack Compose function to display MAX native ads using the Templates API.
 */
@Composable
fun MaxTemplateNativeAdViewComposable(
    adViewState: AdNativeUiState,
    adType: AdType = AdType.MEDIUM
) {
    if (!SHOW_ADS) return
    val context = LocalContext.current
    Crossfade(adViewState, label = "MaxTemplateNativeAdView") { viewState ->
        when (viewState) {
            is AdNativeUiState.LoadError -> {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val titleStyle = MaterialTheme.typography.headlineLarge.bold()
                    val annotatedString = buildAnnotatedString {
                        append("Buy ")

                        withStyle(
                            titleStyle.copy(color = MaterialTheme.colorScheme.primary)
                                .toSpanStyle()
                        ) {
                            append("Wallet+")
                        }
                    }

                    Text(
                        modifier = Modifier,
                        text = annotatedString,
                        style = titleStyle,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        text = "Wallet+ is a paid service that gives you access to all features for the price of a cup of coffee.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Button(
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 12.dp)
                            .fillMaxWidth(),
                        onClick = {
                            context.findActivity().showBillingPlusDialog()
                        },
                    ) {
                        Text("Buy Wallet+")
                    }
                }
            }
            is AdNativeUiState.Loading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Loading()
                    }
                }
            }

            is AdNativeUiState.Success -> {
                viewState.adsView?.let { view ->
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors()
                    ) {
                        AndroidView(
                            factory = {
                                view.also {
                                    if (it.parent != null) (it.parent as ViewGroup).removeView(it)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(adType.height)
                        )
                    }
                }
            }
        }
    }

}

val AdType.height get() = if (this == AdType.MEDIUM) 300.dp else 125.dp

enum class AdType {
    SMALL,
    MEDIUM
}
