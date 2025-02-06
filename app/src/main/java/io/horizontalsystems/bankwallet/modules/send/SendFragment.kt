package io.horizontalsystems.bankwallet.modules.send

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.requireInput
import io.horizontalsystems.bankwallet.entities.Address
import io.horizontalsystems.bankwallet.entities.Wallet
import io.horizontalsystems.bankwallet.modules.amount.AmountInputModeModule
import io.horizontalsystems.bankwallet.modules.amount.AmountInputModeViewModel
import io.horizontalsystems.bankwallet.modules.send.bitcoin.SendBitcoinModule
import io.horizontalsystems.bankwallet.modules.send.bitcoin.SendBitcoinNavHost
import io.horizontalsystems.bankwallet.modules.send.bitcoin.SendBitcoinViewModel
import io.horizontalsystems.bankwallet.modules.send.evm.SendEvmScreen
import io.horizontalsystems.bankwallet.modules.send.solana.SendSolanaModule
import io.horizontalsystems.bankwallet.modules.send.solana.SendSolanaScreen
import io.horizontalsystems.bankwallet.modules.send.solana.SendSolanaViewModel
import io.horizontalsystems.bankwallet.modules.send.ton.SendTonModule
import io.horizontalsystems.bankwallet.modules.send.ton.SendTonScreen
import io.horizontalsystems.bankwallet.modules.send.ton.SendTonViewModel
import io.horizontalsystems.bankwallet.modules.send.tron.SendTronModule
import io.horizontalsystems.bankwallet.modules.send.tron.SendTronScreen
import io.horizontalsystems.bankwallet.modules.send.tron.SendTronViewModel
import io.horizontalsystems.bankwallet.modules.send.zcash.SendZCashModule
import io.horizontalsystems.bankwallet.modules.send.zcash.SendZCashScreen
import io.horizontalsystems.bankwallet.modules.send.zcash.SendZCashViewModel
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

class SendFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.requireInput<Input>() ?: run {
            navController.popBackStack()
            return
        }
        val wallet = input.wallet
        val title = input.title
        val sendEntryPointDestId = input.sendEntryPointDestId
        val address = input.address
        val hideAddress = input.hideAddress
        val amount = input.amount

        val amountInputModeViewModel by navGraphViewModels<AmountInputModeViewModel>(R.id.sendXFragment) {
            AmountInputModeModule.Factory(wallet.coin.uid)
        }

        when (wallet.token.blockchainType) {
            BlockchainType.Bitcoin,
            BlockchainType.BitcoinCash,
            BlockchainType.ECash,
            BlockchainType.Litecoin,
            BlockchainType.Dash -> {
                val factory = SendBitcoinModule.Factory(wallet, address, hideAddress)
                val sendBitcoinViewModel by navGraphViewModels<SendBitcoinViewModel>(R.id.sendXFragment) {
                    factory
                }
                SendBitcoinNavHost(
                    title,
                    findNavController(),
                    sendBitcoinViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    amount
                )
            }

            BlockchainType.Zcash -> {
                val factory = SendZCashModule.Factory(wallet, address, hideAddress)
                val sendZCashViewModel by navGraphViewModels<SendZCashViewModel>(R.id.sendXFragment) {
                    factory
                }
                SendZCashScreen(
                    title,
                    findNavController(),
                    sendZCashViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    amount
                )
            }

            BlockchainType.Ethereum,
            BlockchainType.BinanceSmartChain,
            BlockchainType.Polygon,
            BlockchainType.Avalanche,
            BlockchainType.Optimism,
            BlockchainType.Base,
            BlockchainType.Gnosis,
            BlockchainType.Fantom,
            BlockchainType.ArbitrumOne -> {
                SendEvmScreen(
                    title,
                    findNavController(),
                    amountInputModeViewModel,
                    address,
                    wallet,
                    amount,
                    hideAddress,
                    sendEntryPointDestId
                )
            }

            BlockchainType.Solana -> {
                val factory = SendSolanaModule.Factory(wallet, address, hideAddress)
                val sendSolanaViewModel by navGraphViewModels<SendSolanaViewModel>(R.id.sendXFragment) { factory }
                SendSolanaScreen(
                    title,
                    findNavController(),
                    sendSolanaViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    amount
                )
            }

            BlockchainType.Ton -> {
                val factory = SendTonModule.Factory(wallet, address, hideAddress)
                val sendTonViewModel by navGraphViewModels<SendTonViewModel>(R.id.sendXFragment) { factory }
                SendTonScreen(
                    title,
                    findNavController(),
                    sendTonViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    amount
                )
            }

            BlockchainType.Tron -> {
                val factory = SendTronModule.Factory(wallet, address, hideAddress)
                val sendTronViewModel by navGraphViewModels<SendTronViewModel>(R.id.sendXFragment) { factory }
                SendTronScreen(
                    title,
                    findNavController(),
                    sendTronViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    amount
                )
            }

            else -> {}
        }
    }

    @Parcelize
    data class Input(
        val wallet: Wallet,
        val title: String,
        val sendEntryPointDestId: Int,
        val address: Address,
        val amount: BigDecimal? = null,
        val hideAddress: Boolean = false
    ) : Parcelable

    override val logScreen: String
        get() = "SendFragment"
}
