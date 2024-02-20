package io.horizontalsystems.bankwallet.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KlineResponseItem(

    @Json(name = "stream")
    val stream: String,// Event type
    val data: DataKline
)

@JsonClass(generateAdapter = true)
data class DataKline(
    @Json(name = "E")
    val eventTime: Long,// Event time

    @Json(name = "s")
    val symbol: String,

    @Json(name = "k")
    val candlestick: Candlestick? = null
)

@JsonClass(generateAdapter = true)
data class Candlestick(
    @Json(name = "t")
    val startTime: Long,
    @Json(name = "T")
    val closeTime: Long,
    @Json(name = "s")
    val symbol: String,
    @Json(name = "i")
    val interval: String,
    @Json(name = "f")
    val firstTradeId: Int,
    @Json(name = "L")
    val lastTradeId: Int,
    @Json(name = "o")
    val openPrice: String,
    @Json(name = "c")
    val closePrice: String,
    @Json(name = "h")
    val highPrice: String,
    @Json(name = "l")
    val lowPrice: String,
    @Json(name = "v")
    val baseVolume: String,
    @Json(name = "n")
    val numberOfTrade: Int,
    @Json(name = "x")
    val klineClosed: Boolean,
    @Json(name = "q")
    val quoteVolume: String,
    @Json(name = "V")
    val takeBaseBuy: String,// Taker buy base asset volume
    @Json(name = "Q")
    val takeBuyQuote: String// Taker buy quote asset volume

)
