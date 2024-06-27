package io.horizontalsystems.bankwallet.modules.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billing.BillingClient
import com.android.billing.ToastUtil
import com.android.billing.UserDataRepository
import com.android.billing.models.ProductDetails
import com.android.billing.models.ProductId
import com.android.billing.models.ProductType
import com.android.billing.models.ScreenState
import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import com.android.billing.usecase.ConsumePlusUseCase
import com.android.billing.usecase.PurchasePlusUseCase
import com.android.billing.usecase.VerifyPlusUseCase
import com.android.billingclient.api.Purchase
import com.wallet.blockchain.bitcoin.BuildConfig
import com.wallet.blockchain.bitcoin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@Stable
@HiltViewModel
class BillingPlusViewModel @Inject constructor(
    private val billingClient: BillingClient,
    private val purchasePlusUseCase: PurchasePlusUseCase,
    private val consumePlusUseCase: ConsumePlusUseCase,
    private val verifyPlusUseCase: VerifyPlusUseCase,
    private val userDataRepository: UserDataRepository,
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var _screenState = MutableStateFlow<ScreenState<BillingPlusUiState>>(ScreenState.Loading)

    val screenState = _screenState.asStateFlow()
    companion object{
        private val PREMIUM_MONTH = ProductId(BuildConfig.PREMIUM_MONTH)
    }
    init {
        connectBilling()
    }

    private fun connectBilling() {
        billingClient.initialize(
            callback = {
                viewModelScope.launch {
                    val state = runCatching {
                        val userData = userDataRepository.userData.firstOrNull()

                        BillingPlusUiState(
                            isPlusMode = userData?.isPlusMode ?: false,
                            isDeveloperMode = BuildConfig.DEBUG,
                            productDetails = billingClient.queryProductDetails(
                                PREMIUM_MONTH,
                                ProductType.SUBS
                            ),
                            purchase = runCatching { verifyPlusUseCase.execute(PREMIUM_MONTH) }.getOrNull(),
                        )
                    }.fold(
                        onSuccess = { ScreenState.Idle(it) },
                        onFailure = {
                            Log.w("AAA", it)
                            ScreenState.Error(
                                message = R.string.error_billing,
                                retryTitle = R.string.close,
                            )
                        },
                    )
                    _screenState.emit(
                        state
                    )
                }
            },
            onFailed = {
                _screenState.update {
                    ScreenState.Error(
                        message = R.string.error_billing,
                        retryTitle = R.string.close,
                    )
                }
            }
        )
    }

    suspend fun purchase(activity: Activity): Boolean {
        return runCatching {
            withContext(ioDispatcher) {
                purchasePlusUseCase.execute(activity, PREMIUM_MONTH)
            }
        }.fold(
            onSuccess = {
                userDataRepository.setPlusMode(true)
                ToastUtil.show(activity, R.string.billing_plus_toast_purchased)
                true
            },
            onFailure = {
                Timber.w(it)
                ToastUtil.show(activity, R.string.billing_plus_toast_purchased_error)
                false
            },
        )
    }

    fun onVerify(context: Context) {
        viewModelScope.launch {
            delay(5000)
            verify(context)
        }
    }

    suspend fun verify(context: Context): Boolean {
        return runCatching {
            withContext(ioDispatcher) {
                verifyPlusUseCase.execute(PREMIUM_MONTH)
            }
        }.fold(
            onSuccess = {
                if (it != null) {
                    userDataRepository.setPlusMode(true)
                    ToastUtil.show(context, R.string.billing_plus_toast_verify)
                    true
                } else {
                    userDataRepository.setPlusMode(false)
                    ToastUtil.show(context, R.string.billing_plus_toast_verify_error)
                    false
                }
            },
            onFailure = {
                Timber.w("Billing Error: $it")
                ToastUtil.show(context, R.string.error_billing)
                connectBilling()
                false
            },
        )
    }

    suspend fun consume(context: Context, purchase: Purchase): Boolean {
        return runCatching {
            withContext(ioDispatcher) {
                consumePlusUseCase.execute(purchase)
            }
        }.fold(
            onSuccess = {
                userDataRepository.setPlusMode(false)
                ToastUtil.show(context, R.string.billing_plus_toast_consumed)
                true
            },
            onFailure = {
                Timber.w(it)
                ToastUtil.show(context, R.string.billing_plus_toast_consumed_error)
                false
            },
        )
    }

    override fun onCleared() {
        billingClient.dispose()
    }
}

@Stable
data class BillingPlusUiState(
    val isPlusMode: Boolean = false,
    val isDeveloperMode: Boolean = false,
    val productDetails: ProductDetails? = null,
    val purchase: Purchase? = null,
)
