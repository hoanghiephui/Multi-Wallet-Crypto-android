package io.horizontalsystems.bankwallet.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.horizontalsystems.bankwallet.entities.Currency
import io.horizontalsystems.bankwallet.model.Data
import io.horizontalsystems.bankwallet.ui.TitlePriceView

@Composable
fun TitlePrice(
    currency: Currency,
    data: Data?
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            TitlePriceView(context).apply {
                setCurrency(currency)
                bindView(data)
            }
        },
        update = { view ->
            view.bindView(data)
        }
    )
}
