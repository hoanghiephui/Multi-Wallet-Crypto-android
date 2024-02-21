package io.horizontalsystems.bankwallet.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.wallet.blockchain.bitcoin.R
import com.wallet.blockchain.bitcoin.databinding.ViewTopPriceBinding
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.entities.Currency
import io.horizontalsystems.bankwallet.model.Data
import java.math.BigDecimal

class TitlePriceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private lateinit var currency: Currency
    private val binding = ViewTopPriceBinding.inflate(LayoutInflater.from(context), this)

    fun setCurrency(currency: Currency) {
        this.currency = currency
    }

    fun bindView(data: Data?) {
        data?.let {
            val price = App.numberFormatter.formatFiatFull(
                it.lastPrice?.toBigDecimal() ?: BigDecimal.ZERO,
                currency.symbol
            )
            val highPrice = App.numberFormatter.formatFiatFull(
                it.highPrice?.toBigDecimal() ?: BigDecimal.ZERO,
                currency.symbol
            )
            val lowPrice = App.numberFormatter.formatFiatFull(
                it.lowPrice?.toBigDecimal() ?: BigDecimal.ZERO,
                currency.symbol
            )
            val priceChange = App.numberFormatter.formatFiatFull(
                it.priceChange?.toBigDecimal() ?: BigDecimal.ZERO,
                currency.symbol
            )
            val percentage = it.priceChangePercent?.toBigDecimal() ?: BigDecimal.ZERO
            val priceChangePercent = App.numberFormatter.format(
                percentage,
                0,
                2,
                suffix = "%"
            )
            val openPrice = App.numberFormatter.formatFiatFull(
                it.openPrice?.toBigDecimal() ?: BigDecimal.ZERO,
                currency.symbol
            )
            val color = if (percentage >= BigDecimal.ZERO) R.color.remus else R.color.lucian
            binding.apply {
                tvPrice.text = price
                tvHigh.text = highPrice
                tvLow.text = lowPrice

                tvP.apply {
                    setTextColor(context.getColor(color))
                    text = priceChangePercent
                }
                tvPriceOpen.text = openPrice

                tvVolume.text = App.numberFormatter.format(
                    it.totalTradeBaseVolume?.toBigDecimal() ?: BigDecimal.ZERO,
                    0,
                    2
                )
                textView93.text = "Vol (${it.symbol})"
            }
        }
    }
}
