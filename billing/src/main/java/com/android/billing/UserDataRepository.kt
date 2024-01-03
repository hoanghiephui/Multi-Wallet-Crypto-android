package com.android.billing

import com.android.billing.models.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserDataRepository {
    val userData: Flow<UserData>
    suspend fun setPlusMode(isPlusMode: Boolean)
}

class UserDataRepositoryImpl @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
) : UserDataRepository {
    override val userData: Flow<UserData> = preferencesDataStore.userData

    override suspend fun setPlusMode(isPlusMode: Boolean) {
        preferencesDataStore.setPlusMode(isPlusMode)
    }
}
