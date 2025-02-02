package io.horizontalsystems.bankwallet.ui.compose.components

import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.horizontalsystems.bankwallet.AdNativeUiState
import io.horizontalsystems.bankwallet.core.BaseViewModel.Companion.SHOW_ADS

@Composable
fun NativeAdView(
    modifier: Modifier = Modifier,
    adsState: AdNativeUiState,
    isProcessIndicator: Boolean = true
) {
    AnimatedContent(adsState, label = "native_ads") { state ->
        when (state) {
            AdNativeUiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isProcessIndicator) {
                        CircularProgressIndicator(
                            modifier = Modifier
                        )
                    } else {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .navigationBarsPadding()
                        )
                    }
                }
            }

            AdNativeUiState.LoadError -> {}
            is AdNativeUiState.Success -> {
                state.adsView?.let { view ->
                    AndroidView(
                        factory = {
                            view.also {
                                if (it.parent != null) (it.parent as ViewGroup).removeView(it)
                            }
                        },
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(5.dp))
                    )
                }
            }

            AdNativeUiState.Nothing -> Unit
        }
    }
}
