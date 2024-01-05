package com.android.billing

import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult

fun billingResponse(
    @BillingResponseCode responseCode: Int,
    debugMessage: String = "",
): BillingResponse {
    return BillingResult.newBuilder()
        .setResponseCode(responseCode)
        .setDebugMessage(debugMessage)
        .build()
        .toResponse()
}

sealed class BillingResponse(
    val result: BillingResult,
    val kind: String,
) {
    @BillingResponseCode
    val code: Int = result.responseCode
    val debugMessage: String = result.debugMessage

    override fun toString(): String {
        return "BillingResponse(result=$result, kind='$kind', code=$code, debugMessage='$debugMessage')"
    }

    class Unknown(result: BillingResult) : BillingResponse(result, "Unknown")
    class ServiceTimeout(result: BillingResult) : BillingResponse(result, "ServiceTimeout")
    class FeatureNotSupported(result: BillingResult) : BillingResponse(result, "FeatureNotSupported")
    class ServiceDisconnected(result: BillingResult) : BillingResponse(result, "ServiceDisconnected")
    class OK(result: BillingResult) : BillingResponse(result, "Ok")
    class UserCanceled(result: BillingResult) : BillingResponse(result, "UserCanceled")
    class ServiceUnavailable(result: BillingResult) : BillingResponse(result, "ServiceUnavailable")
    class BillingUnavailable(result: BillingResult) : BillingResponse(result, "BillingUnavailable")
    class ItemUnavailable(result: BillingResult) : BillingResponse(result, "ItemUnavailable")
    class DeveloperError(result: BillingResult) : BillingResponse(result, "DeveloperError")
    class ServiceError(result: BillingResult) : BillingResponse(result, "ServiceError")
    class ItemAlreadyOwned(result: BillingResult) : BillingResponse(result, "ItemAlreadyOwned")
    class ItemNotOwned(result: BillingResult) : BillingResponse(result, "ItemNotOwned")
}

fun BillingResult.toResponse(): BillingResponse {
    return when (responseCode) {
        BillingResponseCode.OK -> BillingResponse.OK(this)
        BillingResponseCode.USER_CANCELED -> BillingResponse.UserCanceled(this)
        BillingResponseCode.NETWORK_ERROR -> BillingResponse.ServiceTimeout(this)
        BillingResponseCode.FEATURE_NOT_SUPPORTED -> BillingResponse.FeatureNotSupported(this)
        BillingResponseCode.SERVICE_DISCONNECTED -> BillingResponse.ServiceDisconnected(this)
        BillingResponseCode.SERVICE_UNAVAILABLE -> BillingResponse.ServiceUnavailable(this)
        BillingResponseCode.BILLING_UNAVAILABLE -> BillingResponse.BillingUnavailable(this)
        BillingResponseCode.ITEM_UNAVAILABLE -> BillingResponse.ItemUnavailable(this)
        BillingResponseCode.DEVELOPER_ERROR -> BillingResponse.DeveloperError(this)
        BillingResponseCode.ERROR -> BillingResponse.ServiceError(this)
        BillingResponseCode.ITEM_ALREADY_OWNED -> BillingResponse.ItemAlreadyOwned(this)
        BillingResponseCode.ITEM_NOT_OWNED -> BillingResponse.ItemNotOwned(this)
        else -> BillingResponse.Unknown(this)
    }
}
