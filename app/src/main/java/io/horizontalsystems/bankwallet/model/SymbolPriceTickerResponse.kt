package io.horizontalsystems.bankwallet.model

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class SymbolPriceTickerResponse(

	@Json(name="symbol")
	val symbol: String? = null,

	@Json(name="price")
	val price: String? = null
)
