package io.horizontalsystems.bankwallet.endpoint

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.horizontalsystems.bankwallet.model.KlineResponseItem
import io.horizontalsystems.bankwallet.model.Subscribe
import io.horizontalsystems.bankwallet.model.TickerStreamRepository
import kotlinx.coroutines.flow.Flow

interface BinanceStream {
    @Receive
    fun observeWebSocket(): Flow<WebSocket.Event>

    @Send
    fun sendSubscribe(subscribe: Subscribe)

    @Receive
    fun observeCandlestick(): Flow<KlineResponseItem>

    @Receive
    fun observeTickerBinance(): Flow<TickerStreamRepository>
}
