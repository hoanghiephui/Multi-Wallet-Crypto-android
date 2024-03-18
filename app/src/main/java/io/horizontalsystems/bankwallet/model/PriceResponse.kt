package io.horizontalsystems.bankwallet.model

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class PriceResponse(

	@Json(name="pagination")
	val pagination: Pagination? = null,

	@Json(name="data")
	val data: List<DataItem>? = null
)

@JsonClass(generateAdapter = true)
data class DataItem(

	@Json(name="base_id")
	val baseId: String? = null,

	@Json(name="currency")
	val currency: String? = null,

	@Json(name="unit_price_scale")
	val unitPriceScale: Int? = null,

	@Json(name="prices")
	val prices: Prices? = null,

	@Json(name="base")
	val base: String? = null
)

@JsonClass(generateAdapter = true)
data class Amount(

	@Json(name="amount")
	val amount: String? = null,

	@Json(name="scale")
	val scale: String? = null,

	@Json(name="currency")
	val currency: String? = null
)

@JsonClass(generateAdapter = true)
data class LatestPrice(

	@Json(name="amount")
	val amount: Amount? = null,

	@Json(name="percent_change")
	val percentChange: PercentChange? = null,

	@Json(name="timestamp")
	val timestamp: String? = null
)

@JsonClass(generateAdapter = true)
data class Prices(

	@Json(name="latest_price")
	val latestPrice: LatestPrice? = null,

	@Json(name="latest")
	val latest: String? = null
)

@JsonClass(generateAdapter = true)
data class PercentChange(

	@Json(name="all")
	val all: Double? = null,

	@Json(name="week")
	val week: Double? = null,

	@Json(name="hour")
	val hour: Double? = null,

	@Json(name="month")
	val month: Double? = null,

	@Json(name="year")
	val year: Double? = null,

	@Json(name="day")
	val day: Double? = null
)

@JsonClass(generateAdapter = true)
data class Pagination(

	@Json(name="limit")
	val limit: Int? = null,

	@Json(name="next_starting_after")
	val nextStartingAfter: String? = null,

	@Json(name="next_uri")
	val nextUri: String? = null,

	@Json(name="timestamp")
	val timestamp: String? = null,

	@Json(name="order")
	val order: String? = null
)
