package com.android.billing

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class CompositePurchasesUpdatedListener internal constructor() : PurchasesUpdatedListener {

    private val listeners = mutableSetOf<PurchasesUpdatedListener>()
    private val lock = ReentrantReadWriteLock()

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        lock.read { listeners.toList() }.forEach { it.onPurchasesUpdated(result, purchases) }
    }

    fun add(listener: PurchasesUpdatedListener) {
        lock.write { listeners.add(listener) }
    }

    fun remove(listener: PurchasesUpdatedListener) {
        lock.write { listeners.remove(listener) }
    }

    fun clear() {
        lock.write { listeners.clear() }
    }
}
