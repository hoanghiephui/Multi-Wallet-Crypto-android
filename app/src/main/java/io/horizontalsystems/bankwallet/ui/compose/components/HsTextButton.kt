package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun HsTextButton(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    CompositionLocalProvider {
        TextButton(
            onClick = onClick
        ) {
            content()
        }
    }
}