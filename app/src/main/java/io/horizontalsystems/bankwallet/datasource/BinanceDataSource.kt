package io.horizontalsystems.bankwallet.datasource

import androidx.annotation.AnyThread
import io.horizontalsystems.bankwallet.endpoint.ApiServiceExecutor
import io.horizontalsystems.bankwallet.model.SymbolPriceTickerResponse
import javax.inject.Inject

interface BinanceDataSource {
    @AnyThread
    suspend fun getSymbolPriceTicker(symbol: String): SymbolPriceTickerResponse

    @AnyThread
    suspend fun getCandlestickData(symbol: String, interval: String): List<List<Any>>
}

class DefaultBinanceDataSource @Inject constructor(
    private val executor: ApiServiceExecutor
): BinanceDataSource {
    override suspend fun getSymbolPriceTicker(symbol: String): SymbolPriceTickerResponse =
        executor.execute { it.getSymbolPriceTicker(symbol) }

    override suspend fun getCandlestickData(symbol: String, interval: String): List<List<Any>> =
        executor.execute { it.getCandlestickData(symbol, interval) }
}
