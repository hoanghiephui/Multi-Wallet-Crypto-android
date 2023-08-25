package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoH3(text: String) {
    headline2_jacob(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        text = text
    )
}

@Composable
fun InfoH1(text: String) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
    ){
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Divider()
    }
}
