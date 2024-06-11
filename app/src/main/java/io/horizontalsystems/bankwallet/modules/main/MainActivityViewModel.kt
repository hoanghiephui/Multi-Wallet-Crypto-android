package io.horizontalsystems.bankwallet.modules.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.walletconnect.web3.wallet.client.Wallet
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.IAccountManager
import io.horizontalsystems.bankwallet.core.ILocalStorage
import io.horizontalsystems.bankwallet.core.managers.UserManager
import io.horizontalsystems.bankwallet.modules.walletconnect.WCDelegate
import io.horizontalsystems.core.IKeyStoreManager
import io.horizontalsystems.core.IPinComponent
import io.horizontalsystems.core.ISystemInfoManager
import io.horizontalsystems.core.security.KeyStoreValidationError
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val userManager: UserManager,
    private val accountManager: IAccountManager,
    private val pinComponent: IPinComponent,
    private val systemInfoManager: ISystemInfoManager,
    private val keyStoreManager: IKeyStoreManager,
    private val localStorage: ILocalStorage
) : ViewModel() {

    val navigateToMainLiveData = MutableLiveData(false)
    val wcEvent = MutableLiveData<Wallet.Model?>()

    init {
        viewModelScope.launch {
            userManager.currentUserLevelFlow.collect {
                navigateToMainLiveData.postValue(true)
            }
        }
        viewModelScope.launch {
            WCDelegate.walletEvents.collect {
                wcEvent.postValue(it)
            }
        }
    }

    fun onWcEventHandled() {
        wcEvent.postValue(null)
    }

    fun validate() {
        if (accountManager.isAccountsEmpty && systemInfoManager.isSystemLockOff) {
            throw when {
                !localStorage.mainShowedOnce -> MainScreenValidationError.Welcome()
                pinComponent.isLocked -> MainScreenValidationError.Unlock()
                else -> MainScreenValidationError.UseAppNotWallet()
            }
        } else {
            if (systemInfoManager.isSystemLockOff) {
                throw MainScreenValidationError.NoSystemLock()
            }

            try {
                keyStoreManager.validateKeyStore()
                if (!localStorage.mainShowedOnce) {
                    throw MainScreenValidationError.Welcome()
                }

                if (pinComponent.isLocked) {
                    throw MainScreenValidationError.Unlock()
                }
                throw MainScreenValidationError.UseAppWallet()
            } catch (e: KeyStoreValidationError.UserNotAuthenticated) {
                throw MainScreenValidationError.UserAuthentication()
            } catch (e: KeyStoreValidationError.KeyIsInvalid) {
                throw MainScreenValidationError.KeyInvalidated()
            }
        }
    }

    fun onNavigatedToMain() {
        navigateToMainLiveData.postValue(false)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(App.userManager, App.accountManager, App.pinComponent, App.systemInfoManager, App.keyStoreManager, App.localStorage) as T
        }
    }
}

sealed class MainScreenValidationError : Exception() {
    class Welcome : MainScreenValidationError()
    class Unlock : MainScreenValidationError()
    class NoSystemLock : MainScreenValidationError()
    class KeyInvalidated : MainScreenValidationError()
    class UserAuthentication : MainScreenValidationError()

    class UseAppNotWallet: MainScreenValidationError()

    class UseAppWallet: MainScreenValidationError()
}
