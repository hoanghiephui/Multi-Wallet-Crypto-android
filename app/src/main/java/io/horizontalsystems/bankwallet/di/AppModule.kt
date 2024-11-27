package io.horizontalsystems.bankwallet.di

import android.content.Context
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.wallet.blockchain.bitcoin.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApplovin(
        @ApplicationContext
        context: Context
    ): AppLovinSdkInitializationConfiguration = AppLovinSdkInitializationConfiguration.builder(
        context.getString(R.string.APPLOVIN_SDK_KEY),
        context
    )
        .setMediationProvider(AppLovinMediationProvider.MAX)
        .build()
}
