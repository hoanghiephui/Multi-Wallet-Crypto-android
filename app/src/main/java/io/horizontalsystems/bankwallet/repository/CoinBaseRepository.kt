package io.horizontalsystems.bankwallet.repository

import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import io.horizontalsystems.bankwallet.datasource.CoinBaseDataSource
import io.horizontalsystems.bankwallet.model.PriceResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface CoinBaseRepository {
    suspend fun getPriceCoin(
        base: String,
        filter: String,
        resolution: String
    ): PriceResponse
}

class CoinBaseRepositoryImpl @Inject constructor(
    private val networkDataSource: CoinBaseDataSource,
    @Dispatcher(AppDispatcher.IO)
    private val dispatcher: CoroutineDispatcher,
) : CoinBaseRepository {
    override suspend fun getPriceCoin(
        base: String,
        filter: String,
        resolution: String
    ): PriceResponse = withContext(dispatcher) {
        networkDataSource.getPriceCoin(base, filter, resolution)
    }
}