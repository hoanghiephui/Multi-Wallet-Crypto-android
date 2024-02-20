package io.horizontalsystems.bankwallet.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.horizontalsystems.bankwallet.datasource.BinanceDataSource
import io.horizontalsystems.bankwallet.datasource.DefaultBinanceDataSource

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {
    @Binds
    fun bindsBinanceDataSource(impl: DefaultBinanceDataSource): BinanceDataSource
}
