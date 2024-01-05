package com.android.billing.usecase

import com.android.billing.BillingClient
import com.android.billing.models.ProductId
import com.android.billing.models.ProductItem
import com.android.billing.models.ProductType
import com.android.billingclient.api.Purchase
import javax.inject.Inject

class VerifyPlusUseCase @Inject constructor(
    private val billingClient: BillingClient,
) {
    suspend fun execute(item: ProductId): Purchase? {
        billingClient.queryPurchaseHistory(ProductType.SUBS)

        val productDetails = billingClient.queryProductDetails(item, ProductType.SUBS)
        val purchases = billingClient.queryPurchases(ProductType.SUBS)

        return purchases.find { it.products.contains(productDetails.productId.toString()) }
    }
}
