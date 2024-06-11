package io.horizontalsystems.bankwallet.modules.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import com.wallet.blockchain.bitcoin.R
import com.walletconnect.web3.wallet.client.Wallet
import dagger.hilt.android.AndroidEntryPoint
import io.horizontalsystems.bankwallet.core.BaseActivity
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.modules.billing.BillingPlusViewModel
import io.horizontalsystems.bankwallet.modules.intro.IntroActivity
import io.horizontalsystems.bankwallet.modules.keystore.KeyStoreActivity
import io.horizontalsystems.bankwallet.modules.lockscreen.LockScreenActivity
import io.horizontalsystems.bankwallet.worker.Sync
import io.horizontalsystems.core.hideKeyboard

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    val viewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModel.Factory()
    }

    override fun onResume() {
        super.onResume()
        validate()
    }

    private val billingViewModel by viewModels<BillingPlusViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController

        navController.setGraph(R.navigation.main_graph, intent.extras)
        navController.addOnDestinationChangedListener { _, _, _ ->
            currentFocus?.hideKeyboard(this)
        }

        viewModel.navigateToMainLiveData.observe(this) {
            if (it) {
                navController.popBackStack(navController.graph.startDestinationId, false)
                viewModel.onNavigatedToMain()
            }
        }

        viewModel.wcEvent.observe(this) { wcEvent ->
            if (wcEvent != null) {
                when (wcEvent) {
                    is Wallet.Model.SessionRequest -> {
                        navController.slideFromBottom(R.id.wcRequestFragment)
                    }
                    is Wallet.Model.SessionProposal -> {
                        navController.slideFromBottom(R.id.wcSessionFragment)
                    }
                    else -> {}
                }

                viewModel.onWcEventHandled()
            }
        }
        billingViewModel.onVerify(this)
        Sync.initialize(this)
    }

    fun validate(
        onUseAppNotWallet: () -> Unit = {},
        onUseAppWallet: () -> Unit = {},
        isWithBalance: Boolean = false
    ) = try {
        viewModel.validate()
    } catch (e: MainScreenValidationError.NoSystemLock) {
        if (isWithBalance) {
            onUseAppNotWallet()
        } else {
            KeyStoreActivity.startForNoSystemLock(this)
            finish()
        }
    } catch (e: MainScreenValidationError.KeyInvalidated) {
        KeyStoreActivity.startForInvalidKey(this)
        finish()
    } catch (e: MainScreenValidationError.UserAuthentication) {
        KeyStoreActivity.startForUserAuthentication(this)
        finish()
    } catch (e: MainScreenValidationError.Welcome) {
        IntroActivity.start(this)
        finish()
    } catch (e: MainScreenValidationError.Unlock) {
        LockScreenActivity.start(this)
    } catch (e: MainScreenValidationError.UseAppNotWallet) {
        onUseAppNotWallet()
    } catch (e: MainScreenValidationError.UseAppWallet) {
        onUseAppWallet()
    }
}
