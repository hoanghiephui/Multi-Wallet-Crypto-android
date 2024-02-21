package io.horizontalsystems.bankwallet.repository

import android.graphics.Color
import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import com.tinder.scarlet.WebSocket
import com.tradingview.lightweightcharts.api.chart.models.color.toIntColor
import com.tradingview.lightweightcharts.api.series.common.SeriesData
import com.tradingview.lightweightcharts.api.series.models.BarData
import com.tradingview.lightweightcharts.api.series.models.HistogramData
import com.tradingview.lightweightcharts.api.series.models.Time
import io.horizontalsystems.bankwallet.datasource.BinanceDataSource
import io.horizontalsystems.bankwallet.endpoint.BinanceStream
import io.horizontalsystems.bankwallet.model.Subscribe
import io.horizontalsystems.bankwallet.model.SymbolPriceTickerResponse
import io.horizontalsystems.bankwallet.model.TickerStreamRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Named

interface BinanceRepository {
    suspend fun getSymbolPriceTicker(symbol: String): SymbolPriceTickerResponse

    suspend fun getCandlestickData(symbol: String, interval: String): List<List<Any>>

    val observeWebSocket: Flow<WebSocket.Event>

    val getSymbolTickerStreams: Flow<TickerStreamRepository>

    val getListSeriesData: Flow<SeriesData>
    val getListVolumeData: Flow<SeriesData>
    fun sendSubscribe(subscribe: Subscribe)
}

class BinanceRepositoryImpl @Inject constructor(
    private val networkDataSource: BinanceDataSource,
    @Dispatcher(AppDispatcher.IO)
    private val dispatcher: CoroutineDispatcher,
    @Named("BINANCE")
    val stream: BinanceStream
): BinanceRepository {

    override suspend fun getSymbolPriceTicker(symbol: String): SymbolPriceTickerResponse =
        withContext(dispatcher) {
            networkDataSource.getSymbolPriceTicker(symbol)
        }

    override suspend fun getCandlestickData(symbol: String, interval: String): List<List<Any>> =
        withContext(dispatcher) {
            networkDataSource.getCandlestickData(symbol, interval)
        }

    override val observeWebSocket: Flow<WebSocket.Event>
        get() = stream.observeWebSocket()
    override val getSymbolTickerStreams: Flow<TickerStreamRepository>
        get() = stream.observeTickerBinance()
            .filter { it.stream?.contains("@ticker") == true && it.data != null }

    override val getListSeriesData: Flow<SeriesData>
        get() = stream.observeCandlestick()
            .filter { it.stream.contains("kline") && it.data.candlestick != null }
            .map {
                val time = it.data.candlestick!!.startTime
                    .convertToTimeUTC()
                BarData(
                    time = Time.Utc(time),
                    open = it.data.candlestick.openPrice.toFloat(),
                    high = it.data.candlestick.highPrice.toFloat(),
                    low = it.data.candlestick.lowPrice.toFloat(),
                    close = it.data.candlestick.closePrice.toFloat(),
                )
            }

    override val getListVolumeData: Flow<SeriesData>
        get() = stream.observeCandlestick()
            .filter { it.stream.contains("kline") && it.data.candlestick != null }
            .map {
                val time = (it.data.candlestick!!.startTime).convertToTimeUTC()
                HistogramData(
                    time = Time.Utc(time),
                    value = it.data.candlestick.baseVolume.toFloat(),
                    color = if (it.data.candlestick.closePrice.toFloat() > it.data.candlestick.openPrice.toFloat())
                        Color.argb(204, 0, 150, 136).toIntColor()
                    else Color.argb(204, 255, 82, 82).toIntColor()
                )
            }

    override fun sendSubscribe(subscribe: Subscribe) {
        stream.sendSubscribe(subscribe)
    }
}

fun Long.convertToTimeUTC(): Long {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    val formatTo = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
        this.timeZone = timeZone
    }
    val timeCurrent = format.format(date)
    return (formatTo.parse(timeCurrent)?.time ?: System.currentTimeMillis()) / 1000
}
