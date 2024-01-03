package com.android.billing.usecase

import android.app.Activity
import com.android.billing.BillingClient
import com.android.billing.ConsumeResult
import com.android.billing.models.ProductDetails
import com.android.billing.models.ProductItem
import com.android.billing.models.ProductType
import com.android.billing.purchaseSingle
import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import com.android.billing.usecase.PurchaseConsumableResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PurchaseDonateUseCase @Inject constructor(
    private val billingClient: BillingClient,
    @Dispatcher(AppDispatcher.Main) private val mainDispatcher: CoroutineDispatcher,
) {
    suspend fun execute(activity: Activity, productType: ProductType): PurchaseConsumableResult {
        val productDetails = billingClient.queryProductDetails(ProductItem.plus, productType)
        val purchaseResult = purchase(activity, productDetails)

        // TODO: verification purchaseResult

        consume(purchaseResult)

        return purchaseResult
    }

    private suspend fun purchase(
        activity: Activity,
        productDetails: ProductDetails,
    ): PurchaseConsumableResult = withContext(mainDispatcher) {
        val command = purchaseSingle(productDetails, null)
        val result = billingClient.launchBillingFlow(activity, command)

        PurchaseConsumableResult(command, productDetails, result.billingPurchase)
    }

    private suspend fun consume(
        purchaseConsumableResult: PurchaseConsumableResult,
    ): ConsumeResult {
        return billingClient.consumePurchase(purchaseConsumableResult.purchase)
    }
}
