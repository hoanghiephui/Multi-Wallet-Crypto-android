package io.horizontalsystems.bankwallet.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.horizontalsystems.bankwallet.repository.BinanceRepository
import io.horizontalsystems.bankwallet.repository.BinanceRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindBinanceRepository(impl: BinanceRepositoryImpl): BinanceRepository
}
