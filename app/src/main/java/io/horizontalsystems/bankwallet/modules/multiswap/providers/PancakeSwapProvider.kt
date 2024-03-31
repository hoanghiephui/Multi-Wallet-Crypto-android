package io.horizontalsystems.bankwallet.modules.multiswap.providers

import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.marketkit.models.BlockchainType

object PancakeSwapProvider : BaseUniswapProvider() {
    override val id = "pancake"
    override val title = "PancakeSwap"
    override val url = "https://pancakeswap.finance/"
    override val icon = R.drawable.pancake

    override fun supports(blockchainType: BlockchainType): Boolean {
        return blockchainType == BlockchainType.BinanceSmartChain
    }
}
