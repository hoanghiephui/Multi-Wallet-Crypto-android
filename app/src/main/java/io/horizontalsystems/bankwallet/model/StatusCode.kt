package io.horizontalsystems.bankwallet.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class StatusCode(val code: Int) {
    INVALID_SYMBOL(-1121),
    UNKNOWN(-1),
    ;

    companion object {
        fun fromCode(code: Int): StatusCode = entries.firstOrNull { it.code == code } ?: UNKNOWN
    }
}
