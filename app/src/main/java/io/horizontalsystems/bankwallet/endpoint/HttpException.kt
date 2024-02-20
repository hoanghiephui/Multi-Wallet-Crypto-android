package io.horizontalsystems.bankwallet.endpoint

import io.horizontalsystems.bankwallet.model.StatusCode

class HttpException(
    val httpCode: Int,
    val statusCode: StatusCode?,
    val statusMessage: String?,
) : RuntimeException("HTTP $httpCode - Code: $statusCode - Message: $statusMessage")
