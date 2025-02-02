package io.horizontalsystems.bankwallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import io.horizontalsystems.bankwallet.core.BaseViewModel.Companion.SHOW_ADS

@Composable
fun rememberInterstitialAd(
    adUnitId: String,
    revenueListener: MaxAdRevenueListener?,
    onDismissAd:  () -> Unit
): MaxInterstitialAd? {
    if (!SHOW_ADS) {
        return null
    }
    val context = LocalContext.current
    val nativeAdLoader = remember(context, adUnitId) {
        MaxInterstitialAd(adUnitId, context).apply {
            revenueListener?.let {
                setRevenueListener(revenueListener)
            }
            setListener(object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {

                }

                override fun onAdDisplayed(p0: MaxAd) {

                }

                override fun onAdHidden(p0: MaxAd) {
                    onDismissAd()
                }

                override fun onAdClicked(p0: MaxAd) {

                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    onDismissAd()
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    onDismissAd()
                }
            })
            loadAd()
        }
    }

    DisposableEffect(nativeAdLoader) {
        onDispose {
            nativeAdLoader.setListener(null)
            nativeAdLoader.setRevenueListener(null)
            nativeAdLoader.destroy()
        }
    }

    // Return the ad state and the reload function
    return remember(revenueListener, nativeAdLoader) {
        nativeAdLoader
    }
}
