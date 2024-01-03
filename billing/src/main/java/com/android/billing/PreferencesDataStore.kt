package com.android.billing

import androidx.datastore.core.DataStore
import com.android.billing.datastore.UserPreference
import com.android.billing.datastore.copy
import com.android.billing.models.UserData
import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferencesDataStore @Inject constructor(
    private val userPreference: DataStore<UserPreference>,
    @Dispatcher(AppDispatcher.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {
    val userData = userPreference.data
        .map {
            UserData(
                billingId = it.billingId,
                isPlusMode = if (it.hasIsPlusMode()) it.isPlusMode else false
            )
        }

    suspend fun setPlusMode(isPlusMode: Boolean) = withContext(ioDispatcher) {
        userPreference.updateData {
            it.copy {
                this.isPlusMode = isPlusMode
            }
        }
    }
}
