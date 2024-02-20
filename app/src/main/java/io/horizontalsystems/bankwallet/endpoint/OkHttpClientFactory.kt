package io.horizontalsystems.bankwallet.endpoint

import okhttp3.Interceptor
import okhttp3.OkHttpClient

internal object OkHttpClientFactory {

    fun create(
        applicationInterceptors: Set<Interceptor>,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .apply {
                applicationInterceptors.forEach { addInterceptor(it) }
            }
            .build()
}
