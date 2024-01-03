package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun LoadingView(
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
