package io.horizontalsystems.bankwallet.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Subscribe(
    val method: String,
    val id: Int,
    val params: List<String>
)

@JsonClass(generateAdapter = true)
data class UnSubscribe(
    val method: String = "UNSUBSCRIBE",
    val id: Int,
    val params: List<String>
)
