package com.android.billing

import com.android.billing.models.FeatureType
import com.android.billing.models.ProductId
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ConsumeParams

sealed class BillingException(
    response: BillingResponse,
    message: String,
) : RuntimeException(message) {
    @BillingClient.BillingResponseCode
    val code = response.code

    val debugMessage = response.debugMessage

    val isServiceDisconnected = response is BillingResponse.ServiceDisconnected

    val isServiceError = response is BillingResponse.ServiceError

    val isDeveloperError = response is BillingResponse.DeveloperError

    val isServiceTimeout = response is BillingResponse.ServiceTimeout

    val isServiceUnavailable = response is BillingResponse.ServiceUnavailable

    val isBillingUnavailable = response is BillingResponse.BillingUnavailable
}

sealed class BillingStepFailedException(
    response: BillingResponse,
    stepName: String,
    details: String,
    val isFailedOnInitialize: Boolean,
    val isCalledAfterDispose: Boolean,
) : BillingException(
    response,
    if (isFailedOnInitialize) {
        if (isCalledAfterDispose) {
            """
                Can not command $stepName caused by already disposed.
                response: $response
                , $details
            """.trimIndent()
        } else {
            """
                Can not command $stepName caused by initialization failed.
                response: $response
                , $details
            """.trimIndent()
        }
    } else {
        """
            Failed to command $stepName.
            response: $response
            , $details
        """.trimIndent()
    },
)

class InitializationFailedException(
    response: BillingResponse,
    isCalledAfterDispose: Boolean = false,
) : BillingException(
    response,
    if (isCalledAfterDispose) {
        "Already disposed by ${response.kind}"
    } else {
        "Failed to initialize caused by ${response.kind}."
    },
)

class VerifyFeatureSupportedFailedException(
    response: BillingResponse,
    feature: FeatureType,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "verifyFeatureSupported",
    "feature: $feature",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    /**
     * Indicates Google Play response was FEATURE_NOT_SUPPORTED
     */
    val isFeatureNotSupported: Boolean = response is BillingResponse.FeatureNotSupported
}

class VerifyFeaturesSupportedFailedException(
    response: BillingResponse,
    result: FeaturesSupportedResult,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "verifyFeaturesSupported",
    "result: $result",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    val isFeatureNotSupported: Boolean = response is BillingResponse.FeatureNotSupported
}

class QueryProductDetailsFailedException(
    response: BillingResponse,
    productId: ProductId,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "queryProductDetails",
    "productId: $productId",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    val isItemUnavailable: Boolean = response is BillingResponse.ItemUnavailable
}

class InternalQueryProductDetailsListFailedException(
    response: BillingResponse,
    command: QueryProductDetailsCommand,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "internalQueryProductDetailsList",
    "params: $command",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    val isItemUnavailable: Boolean = response is BillingResponse.ItemUnavailable
}

class QueryProductDetailsListFailedException(
    response: BillingResponse,
    command: QueryProductDetailsCommand,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "queryProductDetailsList",
    "params: $command",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    val isItemUnavailable: Boolean = response is BillingResponse.ItemUnavailable
}

class QuerySubscriptionOfferDetailsListFailedException(
    response: BillingResponse,
    productId: ProductId,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "querySubscriptionOfferDetailsList",
    "productId: $productId",
    isFailedOnInitialize,
    isCalledAfterDispose,
)

class QueryPurchasesFailedException(
    response: BillingResponse,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "queryPurchases",
    "",
    isFailedOnInitialize,
    isCalledAfterDispose,
)

class QueryPurchaseHistoryFailedException(
    response: BillingResponse,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "queryPurchaseHistory",
    "",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    val isFeatureNotSupported: Boolean = response is BillingResponse.FeatureNotSupported
}

class LaunchBillingFlowFailedException(
    response: BillingResponse,
    command: PurchaseSingleCommand,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "launchBillingFlow",
    "command: $command",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    val isFeatureNotSupported: Boolean = response is BillingResponse.FeatureNotSupported
    val isItemUnavailable: Boolean = response is BillingResponse.ItemUnavailable
    val isUserCancelled: Boolean = response is BillingResponse.UserCanceled
    val isItemAlreadyOwned: Boolean = response is BillingResponse.ItemAlreadyOwned
}

class ConsumePurchaseFailedException(
    response: BillingResponse,
    params: ConsumeParams,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "consumePurchase",
    "params: ConsumeParams(${params.purchaseToken})",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    val isFeatureNotSupported: Boolean = response is BillingResponse.FeatureNotSupported
    val isMightBeExpiredConsumeLimit: Boolean = response is BillingResponse.DeveloperError
}

class AcknowledgePurchaseFailedException(
    response: BillingResponse,
    params: AcknowledgePurchaseParams,
    isFailedOnInitialize: Boolean = false,
    isCalledAfterDispose: Boolean = false,
) : BillingStepFailedException(
    response,
    "acknowledgePurchase",
    "params: AcknowledgePurchaseParams(${params.purchaseToken})",
    isFailedOnInitialize,
    isCalledAfterDispose,
) {
    val isFeatureNotSupported: Boolean = response is BillingResponse.FeatureNotSupported
    val isMightBeExpiredAcknowledgeLimit: Boolean = response is BillingResponse.DeveloperError
}
