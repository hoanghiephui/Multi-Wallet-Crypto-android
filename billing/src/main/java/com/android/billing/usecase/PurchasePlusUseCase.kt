package com.android.billing.usecase

import android.app.Activity
import com.android.billing.AcknowledgeResult
import com.android.billing.BillingClient
import com.android.billing.models.ProductDetails
import com.android.billing.models.ProductId
import com.android.billing.models.ProductItem
import com.android.billing.models.ProductType
import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import com.android.billing.purchaseSingle
import com.android.billing.usecase.PurchaseConsumableResult
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PurchasePlusUseCase @Inject constructor(
    private val billingClient: BillingClient,
    @Dispatcher(AppDispatcher.Main) private val mainDispatcher: CoroutineDispatcher,
) {
    suspend fun execute(activity: Activity,
                        item: ProductId
    ): PurchaseConsumableResult {
        val productDetails = billingClient.queryProductDetails(item, ProductType.SUBS)
        val purchaseResult = purchase(activity, productDetails)

        acknowledge(purchaseResult.purchase)

        return purchaseResult
    }

    private suspend fun purchase(
        activity: Activity,
        productDetails: ProductDetails,
    ): PurchaseConsumableResult = withContext(mainDispatcher) {
        val command = purchaseSingle(productDetails,  productDetails.offers.first().offerToken)
        val result = billingClient.launchBillingFlow(activity, command)

        PurchaseConsumableResult(command, productDetails, result.billingPurchase)
    }

    private suspend fun acknowledge(purchase: Purchase): AcknowledgeResult {
        return billingClient.acknowledgePurchase(purchase)
    }
}
