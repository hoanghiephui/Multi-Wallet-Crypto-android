package com.android.billing.models

import com.android.billingclient.api.ProductDetails as RawProductDetails

data class ProductDetails(
    val productId: ProductId,
    val description: String,
    val name: String,
    val title: String,
    val offers: List<Offer>,
    val rawProductDetails: RawProductDetails,
) {
    data class Offer(
        val basePlanId: BasePlanId,
        val offerId: OfferId,
        val offerToken: String,
        val pricingPhases: List<PricingPhase>,
    )

    data class PricingPhase(
        val formattedPrice: String,
        val priceAmountMicros: Long,
        val priceCurrencyCode: String,
        val billingPeriod: String,
        val billingCycleCount: Int,
        val recurrenceMode: Int,
    )
}

fun List<RawProductDetails>.translate(): List<ProductDetails> {
    return this.map { it.translate() }
}

fun RawProductDetails.translate(): ProductDetails {
    return ProductDetails(
        productId = ProductId(productId),
        description = description,
        name = title,
        title = title,
        offers = this.subscriptionOfferDetails?.map { details ->
            ProductDetails.Offer(
                basePlanId = BasePlanId(details.basePlanId),
                offerId = details.offerId?.let { OfferId.DiscountedOfferId(it) } ?: OfferId.Nothing,
                offerToken = details.offerToken,
                pricingPhases = details.pricingPhases.pricingPhaseList.map {
                    ProductDetails.PricingPhase(
                        formattedPrice = it.formattedPrice,
                        priceAmountMicros = it.priceAmountMicros,
                        priceCurrencyCode = it.priceCurrencyCode,
                        billingPeriod = it.billingPeriod,
                        billingCycleCount = it.billingCycleCount,
                        recurrenceMode = it.recurrenceMode,
                    )
                },
            )
        }.orEmpty(),
        rawProductDetails = this,
    )
}
