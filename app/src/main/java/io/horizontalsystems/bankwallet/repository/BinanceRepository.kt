package io.horizontalsystems.bankwallet.repository

import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import io.horizontalsystems.bankwallet.datasource.BinanceDataSource
import io.horizontalsystems.bankwallet.model.SymbolPriceTickerResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface BinanceRepository {
    suspend fun getSymbolPriceTicker(symbol: String): SymbolPriceTickerResponse
}

class BinanceRepositoryImpl @Inject constructor(
    private val networkDataSource: BinanceDataSource,
    @Dispatcher(AppDispatcher.IO)
    private val dispatcher: CoroutineDispatcher
): BinanceRepository {

    override suspend fun getSymbolPriceTicker(symbol: String): SymbolPriceTickerResponse =
        withContext(dispatcher) {
            networkDataSource.getSymbolPriceTicker(symbol)
        }
}
