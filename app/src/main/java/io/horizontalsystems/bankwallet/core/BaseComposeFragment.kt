package io.horizontalsystems.bankwallet.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.android.billing.UserDataRepository
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import io.horizontalsystems.bankwallet.analytics.AnalyticsHelper
import io.horizontalsystems.bankwallet.analytics.LocalAnalyticsHelper
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseComposeFragment(
    @LayoutRes layoutResId: Int = 0,
    private val screenshotEnabled: Boolean = true
) : Fragment(layoutResId) {
    @Inject
    lateinit var userDataRepository: UserDataRepository

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics
    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )

            setContent {
                CompositionLocalProvider(
                    LocalAnalyticsHelper provides analyticsHelper,
                    androidx.lifecycle.compose.LocalLifecycleOwner provides androidx.compose.ui.platform.LocalLifecycleOwner.current
                ) {
                    ComposeAppTheme {
                        GetContent(findNavController())
                    }
                }
            }
        }
    }

    @Composable
    abstract fun GetContent(navController: NavController)

    override fun onResume() {
        super.onResume()
        if (screenshotEnabled) {
            allowScreenshot()
        } else {
            disallowScreenshot()
        }
    }

    override fun onPause() {
        disallowScreenshot()
        super.onPause()
    }

    private fun allowScreenshot() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    private fun disallowScreenshot() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCurrentScreen(logScreen)
    }

    abstract val logScreen: String

    private fun setCurrentScreen(screenName: String) {
        firebaseAnalytics.apply {
            val bundle = bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                FirebaseAnalytics.Param.SCREEN_NAME to screenName,
            )
            logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        }
    }

}
