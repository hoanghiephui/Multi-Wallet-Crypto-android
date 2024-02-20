package io.horizontalsystems.bankwallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billing.network.AppDispatcher
import com.android.billing.network.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import io.horizontalsystems.bankwallet.repository.BinanceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BinanceViewModel @Inject constructor(
    private val repository: BinanceRepository,
    @Dispatcher(AppDispatcher.Default)
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _binanceState = MutableStateFlow<BinanceAvailable>(BinanceAvailable.Loading)
    val binanceState = _binanceState.asStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BinanceAvailable.Loading,
    )

    fun onBinanceAvailable(symbol: String) {
        viewModelScope.launch(defaultDispatcher) {
            asFlowResult {
                repository.getSymbolPriceTicker(symbol)
            }.safeCollect(
                onEach = {
                    val available = it.data?.price != null
                    _binanceState.emit(BinanceAvailable.StateBinance(available))
                },
                onError = {
                    _binanceState.emit(BinanceAvailable.StateBinance(false))
                }
            )
        }
    }
}


sealed interface BinanceAvailable {
    data object Loading : BinanceAvailable
    data class StateBinance(val available: Boolean) : BinanceAvailable
}
