package com.android.billing.di

import com.android.billing.BillingClient
import com.android.billing.BillingClientImpl
import com.android.billing.BillingClientProvider
import com.android.billing.BillingClientProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface BillingModule {

    @Singleton
    @Binds
    fun bindBillingClientProvider(billingClientProvider: BillingClientProviderImpl): BillingClientProvider

    @Singleton
    @Binds
    fun bindBillingClient(billingClient: BillingClientImpl): BillingClient
}
