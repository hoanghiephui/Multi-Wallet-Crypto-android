package io.horizontalsystems.bankwallet.ui.compose

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class SystemBars(
    val top: Dp = 0.dp,
    val bottom: Dp = 0.dp,
)

val LocalSystemBars = staticCompositionLocalOf { SystemBars() }
