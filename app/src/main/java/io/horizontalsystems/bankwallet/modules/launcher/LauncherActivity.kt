package io.horizontalsystems.bankwallet.modules.launcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.modules.intro.IntroActivity
import io.horizontalsystems.bankwallet.modules.keystore.KeyStoreActivity
import io.horizontalsystems.bankwallet.modules.lockscreen.LockScreenActivity
import io.horizontalsystems.bankwallet.modules.main.MainModule

class LauncherActivity : AppCompatActivity() {
    private val viewModel by viewModels<LaunchViewModel> { LaunchModule.Factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        var uiState: Boolean by mutableStateOf(true)
        loadPage()
        // Keep the splash screen on-screen until the UI state is loaded. This condition is
        // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
        // the UI.
        splashScreen.setKeepOnScreenCondition {
            uiState
        }
    }
    private fun loadPage() {
        when (viewModel.getPageStart()) {
            LaunchViewModel.Page.Welcome -> {
                IntroActivity.start(this)
                finish()
            }
            LaunchViewModel.Page.Main -> {
                openMain()
            }
            LaunchViewModel.Page.Unlock -> {
                val unlockResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    when (result.resultCode) {
                        RESULT_OK -> openMain()
                    }
                }

                val intent = Intent(this, LockScreenActivity::class.java)
                unlockResultLauncher.launch(intent)
            }
            LaunchViewModel.Page.NoSystemLock -> {
                KeyStoreActivity.startForNoSystemLock(this)
            }
            LaunchViewModel.Page.KeyInvalidated -> {
                KeyStoreActivity.startForInvalidKey(this)
            }
            LaunchViewModel.Page.UserAuthentication -> {
                KeyStoreActivity.startForUserAuthentication(this)
            }
        }
    }

    private fun openMain() {
        MainModule.start(this, intent.data)
        intent.data = null
        finish()
    }

}
