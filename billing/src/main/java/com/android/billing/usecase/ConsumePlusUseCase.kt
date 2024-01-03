package com.android.billing.usecase

import com.android.billing.BillingClient
import com.android.billing.ConsumeResult
import com.android.billingclient.api.Purchase
import javax.inject.Inject

class ConsumePlusUseCase @Inject constructor(
    private val billingClient: BillingClient,
) {
    suspend fun execute(purchase: Purchase): ConsumeResult {
        return billingClient.consumePurchase(purchase)
    }
}
