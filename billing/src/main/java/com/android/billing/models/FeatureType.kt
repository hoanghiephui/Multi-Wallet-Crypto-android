package com.android.billing.models

import android.os.Parcelable
import com.android.billingclient.api.BillingClient
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FeatureType(val rawValue: String) : Parcelable {
    SUBSCRIPTIONS(BillingClient.FeatureType.SUBSCRIPTIONS) {
        override fun toString(): String = rawValue
    },
    SUBSCRIPTIONS_UPDATE(BillingClient.FeatureType.SUBSCRIPTIONS_UPDATE) {
        override fun toString(): String = rawValue
    },
    IN_APP_MESSAGING(BillingClient.FeatureType.IN_APP_MESSAGING) {
        override fun toString(): String = rawValue
    },
    PRODUCT_DETAILS(BillingClient.FeatureType.PRODUCT_DETAILS) {
        override fun toString(): String = rawValue
    },
}
