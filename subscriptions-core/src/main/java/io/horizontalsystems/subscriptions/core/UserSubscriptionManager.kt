package io.horizontalsystems.subscriptions.core

import android.app.Activity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object UserSubscriptionManager {
    var authToken: String? = ""

    private val _purchaseStateUpdatedFlow: MutableSharedFlow<Unit> = MutableSharedFlow()
    val purchaseStateUpdatedFlow: SharedFlow<Unit> = _purchaseStateUpdatedFlow.asSharedFlow()

    private val predefinedSubscriptions = listOf(
        Subscription(
            id = "com.blockchain.btc.coinhub_month",
            name = "PRO",
            description = "",
            actions = listOf(
                MultiWallet,
                TokenInsights,
                AdvancedSearch,
                TradeSignals,
                DuressMode,
                AddressPhishing,
                AddressBlacklist,
                PrivacyMode,
                Watchlist
            )
        ),
        Subscription(
            id = "test.subscription_2",
            name = "VIP",
            description = "",
            actions = listOf(
                NoAds,
                MultiWallet,
                TokenInsights,
                AdvancedSearch,
                TradeSignals,
                DuressMode,
                PrivacyMode,
                Watchlist
            )
        ),
    )
    private lateinit var service: SubscriptionService

    suspend fun purchaseStateUpdated() {
        _purchaseStateUpdatedFlow.emit(Unit)
    }

    fun registerService(service: SubscriptionService) {
        service.predefinedSubscriptions = predefinedSubscriptions
        UserSubscriptionManager.service = service
    }

    fun isActionAllowed(paidAction: IPaidAction): Boolean {
        return service.isActionAllowed(paidAction)
    }

    suspend fun getSubscriptions(): List<Subscription> {
        return service.getSubscriptions()
    }

    fun getActiveUserSubscriptions(): List<UserSubscription> {
        return service.getActiveSubscriptions()
    }

    suspend fun launchPurchaseFlow(
        subscriptionId: String,
        offerToken: String,
        activity: Activity
    ): HSPurchase? {
        return service.launchPurchaseFlow(subscriptionId, offerToken, activity)
    }

    fun getBasePlans(subscriptionId: String): List<BasePlan> {
        return service.getBasePlans(subscriptionId)
    }

    suspend fun onResume() {
        service.onResume()
    }

    fun pause() {

    }

    suspend fun restore() {
        service.onResume()
    }
}
