package com.android.billing.models

import androidx.compose.runtime.Stable

@Stable
data class UserData(
    val billingId: String,
    val isPlusMode: Boolean,
) {
    val hasPrivilege get() = isPlusMode
}
