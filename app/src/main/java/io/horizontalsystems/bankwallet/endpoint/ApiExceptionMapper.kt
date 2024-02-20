package io.horizontalsystems.bankwallet.endpoint

import androidx.annotation.AnyThread
import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import io.horizontalsystems.bankwallet.di.MoshiApiService
import io.horizontalsystems.bankwallet.fromJson
import io.horizontalsystems.bankwallet.model.ErrorResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.invoke
import timber.log.Timber
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import retrofit2.HttpException as RetrofitHttpException

class ApiExceptionMapper @Inject constructor(
    @MoshiApiService private val moshi: Moshi,
    @Dispatcher(AppDispatcher.Default)
    private val defaultDispatcher: CoroutineDispatcher,
) {

    @AnyThread
    suspend fun map(error: Exception): Exception =
        when (error) {
            is IOException -> NoNetworkException(error)
            is RetrofitHttpException -> mapHttpException(error)
            else -> UnexpectedException(error)
        }

    @AnyThread
    private suspend fun mapHttpException(error: RetrofitHttpException): HttpException {
        val response = error.errorResponse()
        return HttpException(
            httpCode = error.code(),
            statusCode = response?.statusCode,
            statusMessage = response?.statusMessage,
        )
    }

    @AnyThread
    private suspend fun RetrofitHttpException.errorResponse(): ErrorResponse? = defaultDispatcher {
        response()?.errorBody()?.source()?.let {
            try {
                val json = String(it.readByteArray(), StandardCharsets.UTF_8)
                moshi.fromJson(json)
            } catch (e: JsonDataException) {
                Timber.e(e)
                null
            }
        }
    }
}
