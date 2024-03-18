package io.horizontalsystems.bankwallet.endpoint

import io.horizontalsystems.bankwallet.model.PriceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinBaseEndpoint {
    /**
     * get price coin
     * https://api.coinbase.com/v2/assets/prices?base=VND&filter=listed&resolution=latest
     */
    @GET("v2/assets/prices")
    suspend fun getPriceCoin(
        @Query("base") base: String,
        @Query("filter") filter: String,
        @Query("resolution") resolution: String
    ): PriceResponse
}