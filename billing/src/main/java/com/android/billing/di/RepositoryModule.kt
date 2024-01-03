package com.android.billing.di

import com.android.billing.UserDataRepository
import com.android.billing.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindUserDataRepository(
        userDataRepository: UserDataRepositoryImpl,
    ): UserDataRepository
}
