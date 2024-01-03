package com.android.billing

import com.android.billing.models.ProductDetails
import com.android.billing.models.ProductId
import com.android.billing.models.ProductType
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product

fun purchaseSingle(
    productDetails: ProductDetails,
    offerToken: String?,
    builder: PurchaseSingleCommandBuilder.() -> Unit = {},
): PurchaseSingleCommand {
    return PurchaseSingleCommandBuilder(productDetails, offerToken)
        .apply(builder)
        .build()
}

class PurchaseSingleCommandBuilder(
    val productDetails: ProductDetails,
    val offerToken: String?,
) {
    var obfuscatedAccountId: String? = null
    var obfuscatedProfileId: String? = null
    var subscriptionUpdate: UpdateSubscriptionCommand? = null

    fun build(): PurchaseSingleCommand {
        return PurchaseSingleCommand(
            productDetails = productDetails,
            offerToken = offerToken,
            obfuscatedAccountId = obfuscatedAccountId,
            obfuscatedProfileId = obfuscatedProfileId,
            subscriptionUpdate = subscriptionUpdate,
        )
    }
}

data class PurchaseSingleCommand(
    val productDetails: ProductDetails,
    val offerToken: String?,
    val obfuscatedAccountId: String?,
    val obfuscatedProfileId: String?,
    val subscriptionUpdate: UpdateSubscriptionCommand?,
) {
    val productId: ProductId get() = productDetails.productId

    fun toBillingFlowParams(): BillingFlowParams {
        val productDetailParams = ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails.rawProductDetails)
            .setOfferToken(offerToken ?: "")
            .build()

        val builder = BillingFlowParams.newBuilder()

        obfuscatedAccountId?.let { builder.setObfuscatedAccountId(it) }
        obfuscatedProfileId?.let { builder.setObfuscatedProfileId(it) }
        subscriptionUpdate?.let { builder.setSubscriptionUpdateParams(it.toSubscriptionUpdateParams()) }

        builder.setProductDetailsParamsList(listOf(productDetailParams))

        return builder.build()
    }
}

data class QueryProductDetailsCommand(
    val productIds: List<ProductId>,
) {
    fun toQueryProductDetailsParams(type: ProductType): QueryProductDetailsParams {
        val products = productIds.map {
            Product.newBuilder()
                .setProductId(it.value)
                .setProductType(type.rawValue)
                .build()
        }

        return QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()
    }
}

data class UpdateSubscriptionCommand(
    val oldPurchaseToken: String,
    val prorationMode: Int,
) {
    fun toSubscriptionUpdateParams(): BillingFlowParams.SubscriptionUpdateParams {
        return BillingFlowParams.SubscriptionUpdateParams.newBuilder()
            .setOldPurchaseToken(oldPurchaseToken)
            .setSubscriptionReplacementMode(prorationMode)
            .build()
    }
}
