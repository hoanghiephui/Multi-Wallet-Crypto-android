package io.horizontalsystems.bankwallet.endpoint

import io.horizontalsystems.bankwallet.model.SymbolPriceTickerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceEndpoint {
    @GET("api/v3/ticker/price")
    suspend fun getSymbolPriceTicker(
        @Query("symbol") symbol: String
    ): SymbolPriceTickerResponse

    @GET("api/v3/klines")
    suspend fun getCandlestickData(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String = "5m",
        @Query("limit") limit: Int = 1000
    ): List<List<Any>>
}
