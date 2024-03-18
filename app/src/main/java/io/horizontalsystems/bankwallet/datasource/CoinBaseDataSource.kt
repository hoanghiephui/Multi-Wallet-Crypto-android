package io.horizontalsystems.bankwallet.datasource

import io.horizontalsystems.bankwallet.endpoint.ApiServiceExecutor
import io.horizontalsystems.bankwallet.model.PriceResponse
import javax.inject.Inject

interface CoinBaseDataSource {
    suspend fun getPriceCoin(
        base: String,
        filter: String,
        resolution: String
    ): PriceResponse
}

class DefaultCoinBaseDataSource @Inject constructor(
    private val executor: ApiServiceExecutor
) : CoinBaseDataSource {
    override suspend fun getPriceCoin(
        base: String,
        filter: String,
        resolution: String
    ): PriceResponse =
        executor.executeCoinBase { it.getPriceCoin(base, filter, resolution) }
}