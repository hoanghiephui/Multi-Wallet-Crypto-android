package io.horizontalsystems.bankwallet.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.wallet.blockchain.bitcoin.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import io.horizontalsystems.bankwallet.FlowStreamAdapter
import io.horizontalsystems.bankwallet.endpoint.ApiServiceFactory
import io.horizontalsystems.bankwallet.endpoint.BinanceEndpoint
import io.horizontalsystems.bankwallet.endpoint.BinanceStream
import io.horizontalsystems.bankwallet.endpoint.OkHttpClientFactory
import io.horizontalsystems.bankwallet.endpoint.StatusCodeAdapter
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshiCommon(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            //.add(LocalDateAdapter)
            //.add(DurationAdapter)
            //.add(OffsetDateTimeAdapter)
            //.add(InstantAdapter)
            .build()

    @Provides
    @Singleton
    @MoshiApiService
    fun provideMoshi(moshi: Moshi): Moshi =
        moshi
            .newBuilder()
            .add(StatusCodeAdapter)
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationInterceptorOkHttpClient
        applicationInterceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient = OkHttpClientFactory.create(
        applicationInterceptors = applicationInterceptors
    )

    @ApplicationInterceptorOkHttpClient
    @IntoSet
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): Interceptor =
        HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        }

    @Provides
    @Named("okHttpWebSocket")
    @Singleton
    fun provideHttpWebSocket(
        @ApplicationInterceptorOkHttpClient
        httpLoggingInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideBinanceService(
        okHttpClient: OkHttpClient,
        @MoshiApiService moshi: Moshi,
    ): BinanceEndpoint =
        ApiServiceFactory.create(
            baseUrl = "https://www.binance.com/",
            okHttpClient = okHttpClient,
            moshi = moshi,
        )

    @Provides
    @Singleton
    fun createScarletInstance(
        moshi: Moshi
    ): Scarlet.Builder {
        return Scarlet.Builder()
            .backoffStrategy(ExponentialBackoffStrategy(2000, 4000))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(FlowStreamAdapter.Factory)
    }

    @Provides
    @Named("BINANCE")
    @Singleton
    fun createBinanceService(
        scarlet: Scarlet.Builder,
        okHttpClient: OkHttpClient
    ): BinanceStream {
        return scarlet
            .webSocketFactory(okHttpClient.newWebSocketFactory("wss://stream.binance.com/stream"))
            .build()
            .create()
    }
}
