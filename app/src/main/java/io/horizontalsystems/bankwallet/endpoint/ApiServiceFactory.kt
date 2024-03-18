package io.horizontalsystems.bankwallet.endpoint

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiServiceFactory {
    fun create(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): BinanceEndpoint =
        setupRetrofit(
            baseUrl = baseUrl,
            okHttpClient = okHttpClient,
            moshi = moshi,
        ).create(BinanceEndpoint::class.java)

    fun createCoinBase(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): CoinBaseEndpoint =
        setupRetrofit(
            baseUrl = baseUrl,
            okHttpClient = okHttpClient,
            moshi = moshi,
        ).create(CoinBaseEndpoint::class.java)

    private fun setupRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit {

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }
}
