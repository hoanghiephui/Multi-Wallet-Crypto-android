package com.android.billing.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductId(val value: String) : Parcelable {
    override fun toString(): String = value
}

object ProductItem {
    // premium mode
    val plus = ProductId("premium_mode")

    // donation
    val love = ProductId("donate_love")
    val coffee = ProductId("donate_coffee")
    val hamburger = ProductId("donate_hamburger")
    val pizza = ProductId("donate_pizza")
    val meal = ProductId("donate_meal")
    val sample = ProductId("android.test.purchased")

    //sub
    val month = ProductId("com.wallet.blockchain.bitcoin_month")
}
