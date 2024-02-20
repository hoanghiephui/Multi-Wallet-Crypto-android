package io.horizontalsystems.bankwallet.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "code")
    val statusCode: StatusCode,
    @Json(name = "msg")
    val statusMessage: String,
)
