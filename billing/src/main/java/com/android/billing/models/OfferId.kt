package com.android.billing.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface OfferId : Parcelable {
    @Parcelize
    object Nothing : OfferId {
        override fun toString(): String = ""
    }

    @Parcelize
    data class DiscountedOfferId(internal val value: String) : OfferId {
        override fun toString(): String = value
    }
}
