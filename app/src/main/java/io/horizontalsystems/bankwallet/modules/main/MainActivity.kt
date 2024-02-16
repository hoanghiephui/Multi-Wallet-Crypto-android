package io.horizontalsystems.bankwallet.modules.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.wallet.blockchain.bitcoin.R
import dagger.hilt.android.AndroidEntryPoint
import io.horizontalsystems.bankwallet.core.BaseActivity
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.modules.billing.BillingPlusViewModel
import io.horizontalsystems.bankwallet.modules.billing.showBillingPlusDialog
import io.horizontalsystems.bankwallet.modules.walletconnect.AuthEvent
import io.horizontalsystems.bankwallet.modules.walletconnect.SignEvent
import io.horizontalsystems.bankwallet.modules.walletconnect.WCViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModel.Factory()
    }

    private val billingViewModel by viewModels<BillingPlusViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val wcViewModel = WCViewModel()

        setContentView(R.layout.activity_main)

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController

        navController.setGraph(R.navigation.main_graph, intent.extras)
        navController.addOnDestinationChangedListener(this)
        handleWeb3WalletEvents(navController, wcViewModel)

        viewModel.navigateToMainLiveData.observe(this) {
            if (it) {
                navController.popBackStack(navController.graph.startDestinationId, false)
                viewModel.onNavigatedToMain()
            }
        }
        billingViewModel.onVerify(this)
        if (intent?.extras?.getBoolean("plus") == true) {
            showBillingPlusDialog()
            intent?.extras?.remove("plus")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.extras?.getBoolean("plus") == true) {
            showBillingPlusDialog()
        }
    }

    private fun handleWeb3WalletEvents(
        navController: NavController,
        wcViewModel: WCViewModel,
    ) {
        wcViewModel.walletEvents
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { event ->
                when (event) {
                    is SignEvent.SessionProposal -> navController.slideFromBottom(R.id.wcSessionFragment)
                    is SignEvent.SessionRequest -> {
                        navController.slideFromBottom(R.id.wcRequestFragment,)
                    }

                    is SignEvent.Disconnect -> {
                    }

                    is AuthEvent.OnRequest -> {
                    }

                    else -> Unit
                }
            }
            .launchIn(lifecycleScope)
    }
}
