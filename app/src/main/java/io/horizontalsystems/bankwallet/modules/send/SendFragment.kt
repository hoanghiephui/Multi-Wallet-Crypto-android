package io.horizontalsystems.bankwallet.modules.send

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.BaseComposeFragment
import io.horizontalsystems.bankwallet.core.requireInput
import io.horizontalsystems.bankwallet.entities.Wallet
import io.horizontalsystems.bankwallet.modules.amount.AmountInputModeModule
import io.horizontalsystems.bankwallet.modules.amount.AmountInputModeViewModel
import io.horizontalsystems.bankwallet.modules.send.binance.SendBinanceModule
import io.horizontalsystems.bankwallet.modules.send.binance.SendBinanceScreen
import io.horizontalsystems.bankwallet.modules.send.binance.SendBinanceViewModel
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
import io.horizontalsystems.bankwallet.modules.sendtokenselect.PrefilledData
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.parcelize.Parcelize

class SendFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent(navController: NavController) {
        val input = navController.requireInput<Input>()
        val wallet = input.wallet
        val title = input.title
        val sendEntryPointDestId = input.sendEntryPointDestId
        val predefinedAddress = input.predefinedAddress
        val prefilledData = input.prefilledAddressData

        val amountInputModeViewModel by navGraphViewModels<AmountInputModeViewModel>(R.id.sendXFragment) {
            AmountInputModeModule.Factory(wallet.coin.uid)
        }

        when (wallet.token.blockchainType) {
            BlockchainType.Bitcoin,
            BlockchainType.BitcoinCash,
            BlockchainType.ECash,
            BlockchainType.Litecoin,
            BlockchainType.Dash -> {
                val factory = SendBitcoinModule.Factory(wallet, predefinedAddress)
                val sendBitcoinViewModel by navGraphViewModels<SendBitcoinViewModel>(R.id.sendXFragment) {
                    factory
                }
                SendBitcoinNavHost(
                    title,
                    findNavController(),
                    sendBitcoinViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    prefilledData,
                )
            }

            is BlockchainType.BinanceChain -> {
                val factory = SendBinanceModule.Factory(wallet, predefinedAddress)
                val sendBinanceViewModel by navGraphViewModels<SendBinanceViewModel>(R.id.sendXFragment) {
                    factory
                }
                SendBinanceScreen(
                    title,
                    findNavController(),
                    sendBinanceViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    prefilledData,
                )
            }

            BlockchainType.Zcash -> {
                val factory = SendZCashModule.Factory(wallet, predefinedAddress)
                val sendZCashViewModel by navGraphViewModels<SendZCashViewModel>(R.id.sendXFragment) {
                    factory
                }
                SendZCashScreen(
                    title,
                    findNavController(),
                    sendZCashViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    prefilledData,
                )
            }

            BlockchainType.Ethereum,
            BlockchainType.BinanceSmartChain,
            BlockchainType.Polygon,
            BlockchainType.Avalanche,
            BlockchainType.Optimism,
            BlockchainType.Gnosis,
            BlockchainType.Fantom,
            BlockchainType.ArbitrumOne -> {
                SendEvmScreen(
                    title,
                    findNavController(),
                    amountInputModeViewModel,
                    prefilledData,
                    wallet,
                    predefinedAddress
                )
            }

            BlockchainType.Solana -> {
                val factory = SendSolanaModule.Factory(wallet, predefinedAddress)
                val sendSolanaViewModel by navGraphViewModels<SendSolanaViewModel>(R.id.sendXFragment) { factory }
                SendSolanaScreen(
                    title,
                    findNavController(),
                    sendSolanaViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    prefilledData,
                )
            }

            BlockchainType.Ton -> {
                val factory = SendTonModule.Factory(wallet, predefinedAddress)
                val sendTonViewModel by navGraphViewModels<SendTonViewModel>(R.id.sendXFragment) { factory }
                SendTonScreen(
                    title,
                    findNavController(),
                    sendTonViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    prefilledData,
                )
            }

            BlockchainType.Tron -> {
                val factory = SendTronModule.Factory(wallet, predefinedAddress)
                val sendTronViewModel by navGraphViewModels<SendTronViewModel>(R.id.sendXFragment) { factory }
                SendTronScreen(
                    title,
                    findNavController(),
                    sendTronViewModel,
                    amountInputModeViewModel,
                    sendEntryPointDestId,
                    prefilledData,
                )
            }

            else -> {}
        }
    }

    @Parcelize
    data class Input(
        val wallet: Wallet,
        val title: String,
        val sendEntryPointDestId: Int = 0,
        val predefinedAddress: String? = null,
        val prefilledAddressData: PrefilledData? = null
    ) : Parcelable

    override val logScreen: String
        get() = "SendFragment"
}
