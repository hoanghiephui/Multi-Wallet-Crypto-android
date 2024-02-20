package io.horizontalsystems.bankwallet.endpoint

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import io.horizontalsystems.bankwallet.model.StatusCode

internal object StatusCodeAdapter {

    @FromJson
    fun fromJson(code: Int): StatusCode =
        StatusCode.fromCode(code)

    @ToJson
    fun toJson(statusCode: StatusCode): Int =
        statusCode.code
}
