package com.android.billing.models

import android.os.Parcelable
import com.android.billingclient.api.BillingClient
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ProductType(val rawValue: String) : Parcelable {
    SUBS(BillingClient.ProductType.SUBS) {
        override fun toString(): String = rawValue
    },
    INAPP(BillingClient.ProductType.INAPP) {
        override fun toString(): String = rawValue
    },
}
