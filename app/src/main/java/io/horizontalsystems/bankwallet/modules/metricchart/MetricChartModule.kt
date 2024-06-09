package io.horizontalsystems.bankwallet.modules.metricchart

import android.os.Parcelable
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.modules.market.ImageSource
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MetricsType : Parcelable {
    TotalMarketCap, Volume24h, Etf, TvlInDefi;
}
