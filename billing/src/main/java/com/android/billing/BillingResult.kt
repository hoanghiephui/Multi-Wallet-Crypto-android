package com.android.billing

import com.android.billing.models.FeatureType
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase

data class FeaturesSupportedResult(
    val resultSet: Map<FeatureType, Boolean>,
) {
    fun featureTypes() = resultSet.keys.toList()
    fun isSupported(featureType: FeatureType) = resultSet[featureType] ?: false
    fun isSupported() = resultSet.values.all { it }
}

data class SingleBillingFlowResult(
    val command: PurchaseSingleCommand,
    val purchases: List<Purchase>,
) {
    val billingProductId get() = command.productId
    val billingPurchase get() = purchases.first { it.products.contains(billingProductId.value) && !it.isAcknowledged }
}

data class ConsumeResult(
    val isItemNotOwned: Boolean,
    val params: ConsumeParams,
)

data class AcknowledgeResult(
    val params: AcknowledgePurchaseParams,
)
