package io.horizontalsystems.bankwallet.endpoint

import androidx.annotation.AnyThread
import timber.log.Timber
import javax.inject.Inject

class ApiServiceExecutor @Inject constructor(
    private val binanceEndpoint: BinanceEndpoint,
    private val coinBaseEndpoint: CoinBaseEndpoint,
    private val apiExceptionMapper: ApiExceptionMapper,
) {
    @AnyThread
    suspend fun <T> execute(
        mapHttpException: ((HttpException) -> Exception?)? = null,
        request: suspend (BinanceEndpoint) -> T,
    ): T =
        try {
            request(binanceEndpoint)
        } catch (e: Exception) {
            Timber.e(e)
            throw apiExceptionMapper.map(e).let {
                if (it is HttpException) {
                    mapHttpException?.invoke(it) ?: it
                } else {
                    it
                }
            }
        }

    @AnyThread
    suspend fun <T> executeCoinBase(
        mapHttpException: ((HttpException) -> Exception?)? = null,
        request: suspend (CoinBaseEndpoint) -> T,
    ): T =
        try {
            request(coinBaseEndpoint)
        } catch (e: Exception) {
            Timber.e(e)
            throw apiExceptionMapper.map(e).let {
                if (it is HttpException) {
                    mapHttpException?.invoke(it) ?: it
                } else {
                    it
                }
            }
        }
}
