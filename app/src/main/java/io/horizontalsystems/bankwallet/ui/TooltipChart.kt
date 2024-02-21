package io.horizontalsystems.bankwallet.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.wallet.blockchain.bitcoin.R
import com.wallet.blockchain.bitcoin.databinding.TooltipLayoutBinding

class TooltipChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = TooltipLayoutBinding.inflate(LayoutInflater.from(context), this)

    init {
        this.background = ContextCompat.getDrawable(context, R.drawable.bg_ads)
    }

    fun setHigh(value: String) {
        binding.tvHigh.text = value
    }

    fun setDate(value: String) {
        binding.tvTime.text = value
    }

    fun setOpen(value: String) {
        binding.tvOpen.text = value
    }

    fun setClose(value: String) {
        binding.tvClose.text = value
    }

    fun setLow(value: String) {
        binding.tvLow.text = value
    }

    fun setVolume(value: String) {
        binding.tvVolume.text = value
    }
}
