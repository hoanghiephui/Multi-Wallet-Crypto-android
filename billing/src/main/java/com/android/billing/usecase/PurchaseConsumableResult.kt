package com.android.billing.usecase

import com.android.billing.PurchaseSingleCommand
import com.android.billing.models.ProductDetails
import com.android.billingclient.api.Purchase

data class PurchaseConsumableResult(
    val command: PurchaseSingleCommand,
    val productDetails: ProductDetails,
    val purchase: Purchase,
)
