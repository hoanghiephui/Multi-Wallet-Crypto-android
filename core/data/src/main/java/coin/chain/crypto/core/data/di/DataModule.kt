package coin.chain.crypto.core.data.di

import coin.chain.crypto.core.data.repository.OfflineFirstUserDataRepository
import coin.chain.crypto.core.data.repository.UserDataRepository
import coin.chain.crypto.core.data.util.ConnectivityManagerNetworkMonitor
import coin.chain.crypto.core.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
    @Binds
    fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository
}