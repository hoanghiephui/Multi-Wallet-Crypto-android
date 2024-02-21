package io.horizontalsystems.bankwallet.model

import com.tradingview.lightweightcharts.api.series.common.SeriesData
import com.tradingview.lightweightcharts.api.series.enums.SeriesType

data class DataChart(
    val list: List<SeriesData>,
    val type: SeriesType
)
