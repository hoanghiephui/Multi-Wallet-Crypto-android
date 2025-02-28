package io.horizontalsystems.subscriptions.core

import android.app.Activity
import android.content.Context

object UserSubscriptionManager {
    var authToken: String? = ""

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
                Watchlist
            )
        ),
    )
    private lateinit var service: SubscriptionService

    val activeSubscriptionStateFlow
        get() = service.activeSubscriptionStateFlow

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

    suspend fun launchPurchaseFlow(
        subscriptionId: String,
        offerToken: String,
        activity: Activity
    ): HSPurchase? {
        return service.launchPurchaseFlow(subscriptionId, offerToken, activity)
    }

    fun launchManageSubscriptionScreen(context: Context) {
        service.launchManageSubscriptionScreen(context)
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
