package io.horizontalsystems.bankwallet.modules.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.billing.BillingClient
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.managers.UserManager
import kotlinx.coroutines.launch

class MainActivityViewModel(
    userManager: UserManager,
    billingClient: BillingClient
) : ViewModel() {

    val navigateToMainLiveData = MutableLiveData(false)

    init {
        viewModelScope.launch {
            userManager.currentUserLevelFlow.collect {
                navigateToMainLiveData.postValue(true)
            }
        }
        billingClient.initialize()
    }

    fun onNavigatedToMain() {
        navigateToMainLiveData.postValue(false)
    }

    class Factory(private val billingClient: BillingClient) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(App.userManager, billingClient) as T
        }
    }
}
