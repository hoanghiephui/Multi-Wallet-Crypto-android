package io.horizontalsystems.bankwallet.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TickerStreamRepository(
    @Json(name = "data")
    val data: Data? = null,

    @Json(name = "stream")
    val stream: String? = null
)

@JsonClass(generateAdapter = true)
data class Data(

    @Json(name = "a")
    val bestAskPrice: String? = null,

    @Json(name = "A")
    val bestAskQuantity: String? = null,

    @Json(name = "b")
    val bestBidPrice: String? = null,

    @Json(name = "B")
    val bestBidQuantity: String? = null,

    @Json(name = "c")
    val lastPrice: String? = null,

    @Json(name = "C")
    val statisticCloseTime: Long? = null,

    @Json(name = "e")
    val eventType: String? = null,

    @Json(name = "E")
    val eventTime: Long? = null,

    @Json(name = "F")
    val firstTradeId: Int? = null,

    @Json(name = "h")
    val highPrice: String? = null,

    @Json(name = "l")
    val lowPrice: String? = null,

    @Json(name = "L")
    val lastTradeId: Int? = null,

    @Json(name = "n")
    val totalNumberOfTrade: Int? = null,

    @Json(name = "o")
    val openPrice: String? = null,

    @Json(name = "O")
    val statisticOpenTime: Long? = null,

    @Json(name = "p")
    val priceChange: String? = null,

    @Json(name = "P")
    val priceChangePercent: String? = null,

    @Json(name = "Q")
    val lastQuantity: String? = null,

    @Json(name = "q")
    val totalTradeQuote: String? = null,// Total traded quote asset volume

    @Json(name = "s")
    val symbol: String? = null,

    @Json(name = "v")
    val totalTradeBaseVolume: String? = null, // Total traded base asset volume

    @Json(name = "w")
    val weightedAveragePrice: String? = null,

    @Json(name = "x")
    val firstTrade: String? = null// First trade(F)-1 price (first trade before the 24hr rolling window)
)
