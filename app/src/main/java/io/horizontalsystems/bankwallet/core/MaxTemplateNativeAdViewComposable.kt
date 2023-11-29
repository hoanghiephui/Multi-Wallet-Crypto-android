package io.horizontalsystems.bankwallet.core

import android.content.Context
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import io.horizontalsystems.bankwallet.ui.compose.bold

/**
 * Ad loader to load Max Native ads with Templates API using Jetpack Compose.
 */
class MaxTemplateNativeAdViewComposableLoader(
    private val viewModel: BaseViewModel
) {
    var nativeAdView = mutableStateOf<AdViewState>(AdViewState.Default)
    private var nativeAd: MaxAd? = null
    private lateinit var nativeAdLoader: MaxNativeAdLoader
    fun destroy() {
        // Must destroy native ad or else there will be memory leaks.
        if (nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            nativeAdLoader.destroy(nativeAd)
        }

        // Destroy the actual loader itself
        nativeAdLoader.destroy()
    }

    fun loadAd(
        context: Context,
        adUnitIdentifier: String,
    ) {
        nativeAdLoader = MaxNativeAdLoader(adUnitIdentifier, context)

        val adListener = object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(loadedNativeAdView: MaxNativeAdView?, ad: MaxAd) {
                viewModel.logCallback()
                // Cleanup any pre-existing native ad to prevent memory leaks.
                if (nativeAd != null) {
                    nativeAdLoader.destroy(nativeAd)
                    nativeAdView.value.let {
                        if (it is AdViewState.LoadAd) {
                            it.adView.removeAllViews()
                            it.adView.addView(loadedNativeAdView)
                        }
                    }
                }

                nativeAd = ad // Save ad for cleanup.
                loadedNativeAdView?.let {
                    nativeAdView.value = AdViewState.LoadAd(loadedNativeAdView)
                }
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                viewModel.logCallback()
            }

            override fun onNativeAdClicked(ad: MaxAd) {
                viewModel.logCallback()
            }

            override fun onNativeAdExpired(nativeAd: MaxAd) {
                viewModel.logCallback()
            }
        }
        nativeAdLoader.apply {
            setNativeAdListener(adListener)
        }
        nativeAdLoader.loadAd()
    }
}

/**
 * Jetpack Compose function to display MAX native ads using the Templates API.
 */
@Composable
fun MaxTemplateNativeAdViewComposable(adViewState: AdViewState) {
    Crossfade(adViewState, label = "MaxTemplateNativeAdView") { viewState ->
        when (viewState) {
            is AdViewState.Default -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
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
                                .padding(top = 24.dp, bottom = 24.dp)
                                .fillMaxWidth(),
                            onClick = { },
                        ) {
                            Text("Sub")
                        }
                    }
                }
            }

            is AdViewState.LoadAd -> {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors()
                ) {
                    AndroidView(
                        factory = {
                            viewState.adView.apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                this.mainView.setBackgroundColor(
                                    ContextCompat.getColor(
                                        it,
                                        android.R.color.transparent
                                    )
                                )
                            }.also {
                                if (it.parent != null) (it.parent as ViewGroup).removeView(it)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            }
        }
    }

}

sealed interface AdViewState {
    data class LoadAd(
        val adView: MaxNativeAdView
    ) : AdViewState

    data object Default : AdViewState
}
