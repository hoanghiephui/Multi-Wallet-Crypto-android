package io.horizontalsystems.bankwallet.modules.sendevmtransaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.AppLogger
import io.horizontalsystems.bankwallet.core.EvmError
import io.horizontalsystems.bankwallet.core.badge
import io.horizontalsystems.bankwallet.core.convertedError
import io.horizontalsystems.bankwallet.core.ethereum.CautionViewItem
import io.horizontalsystems.bankwallet.core.ethereum.CautionViewItemFactory
import io.horizontalsystems.bankwallet.core.ethereum.EvmCoinServiceFactory
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.core.stats.StatSection
import io.horizontalsystems.bankwallet.modules.contacts.ContactsRepository
import io.horizontalsystems.bankwallet.modules.contacts.model.Contact
import io.horizontalsystems.bankwallet.modules.send.SendModule
import io.horizontalsystems.bankwallet.modules.send.evm.SendEvmData
import io.horizontalsystems.bankwallet.modules.send.evm.SendEvmData.AdditionalInfo
import io.horizontalsystems.core.toHexString
import io.horizontalsystems.erc20kit.decorations.ApproveEip20Decoration
import io.horizontalsystems.erc20kit.decorations.OutgoingEip20Decoration
import io.horizontalsystems.ethereumkit.decorations.OutgoingDecoration
import io.horizontalsystems.ethereumkit.decorations.TransactionDecoration
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.nftkit.decorations.OutgoingEip1155Decoration
import io.horizontalsystems.nftkit.decorations.OutgoingEip721Decoration
import io.horizontalsystems.oneinchkit.decorations.OneInchDecoration
import io.horizontalsystems.oneinchkit.decorations.OneInchSwapDecoration
import io.horizontalsystems.oneinchkit.decorations.OneInchUnoswapDecoration
import io.horizontalsystems.uniswapkit.decorations.SwapDecoration
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import java.math.BigInteger

class SendEvmTransactionViewModel(
    val service: ISendEvmTransactionService,
    private val coinServiceFactory: EvmCoinServiceFactory,
    private val cautionViewItemFactory: CautionViewItemFactory,
    private val contactsRepo: ContactsRepository,
    private val blockchainType: BlockchainType
) : ViewModel() {
    val sendEnabledLiveData = MutableLiveData(false)

    val sendingLiveData = MutableLiveData<Unit>()
    val sendSuccessLiveData = MutableLiveData<ByteArray>()
    val sendFailedLiveData = MutableLiveData<String>()
    val cautionsLiveData = MutableLiveData<List<CautionViewItem>>()

    val viewItemsLiveData = MutableLiveData<List<SectionViewItem>>()

    init {
        viewModelScope.launch {
            service.stateObservable.asFlow().collect {
                sync(it)
            }
        }
        viewModelScope.launch {
            service.sendStateObservable.asFlow().collect {
                sync(it)
            }
        }

        sync(service.state)
        sync(service.sendState)

        viewModelScope.launch {
            contactsRepo.contactsFlow.collect {
                sync(service.state)
            }
        }
        viewModelScope.launch {
            service.start()
        }
    }

    fun send(logger: AppLogger) {
        service.send(logger)
    }

    override fun onCleared() {
        service.clear()
    }

    @Synchronized
    private fun sync(state: SendEvmTransactionService.State) {
        when (state) {
            is SendEvmTransactionService.State.Ready -> {
                sendEnabledLiveData.postValue(true)
                cautionsLiveData.postValue(cautionViewItemFactory.cautionViewItems(state.warnings, errors = listOf()))
            }
            is SendEvmTransactionService.State.NotReady -> {
                sendEnabledLiveData.postValue(false)
                cautionsLiveData.postValue(cautionViewItemFactory.cautionViewItems(state.warnings, state.errors))
            }
        }

        viewItemsLiveData.postValue(getItems(service.txDataState))
    }

    private fun getItems(dataState: SendEvmTransactionService.TxDataState): List<SectionViewItem> {
        val additionalInfo = dataState.additionalInfo

        var sections: List<SectionViewItem> = dataState.decoration?.let {
            getViewItems(it, additionalInfo)
        } ?: listOf()

        if (sections.isEmpty()) {
            if (dataState.transactionData != null) {
                sections = getUnknownMethodItems(
                    dataState.transactionData,
                    service.methodName(dataState.transactionData.input)
                )
            }
        }

        additionalInfo?.walletConnectInfo?.let {
            sections = sections + getWalletConnectSectionView(it)
        }

        return sections
    }

    private fun sync(sendState: SendEvmTransactionService.SendState) =
        when (sendState) {
            SendEvmTransactionService.SendState.Idle -> Unit
            SendEvmTransactionService.SendState.Sending -> {
                sendEnabledLiveData.postValue(false)
                sendingLiveData.postValue(Unit)
            }
            is SendEvmTransactionService.SendState.Sent -> sendSuccessLiveData.postValue(sendState.transactionHash)
            is SendEvmTransactionService.SendState.Failed -> sendFailedLiveData.postValue(
                convertError(sendState.error)
            )
        }

    private fun getViewItems(
        decoration: TransactionDecoration,
        additionalInfo: AdditionalInfo?
    ): List<SectionViewItem>? =
        when (decoration) {
            is OutgoingDecoration -> getSendBaseCoinItems(
                decoration.to,
                decoration.value
            )

            is OutgoingEip20Decoration -> getEip20TransferViewItems(
                decoration.to,
                decoration.value,
                decoration.contractAddress
            )

            is ApproveEip20Decoration -> getEip20ApproveViewItems(
                decoration.spender,
                decoration.value,
                decoration.contractAddress
            )

            is SwapDecoration -> getUniswapViewItems(
                decoration.amountIn,
                decoration.amountOut,
                decoration.tokenIn,
                decoration.tokenOut,
                decoration.recipient
            )

            is OneInchSwapDecoration -> getOneInchSwapViewItems(
                decoration.tokenIn,
                decoration.tokenOut,
                decoration.amountIn,
                decoration.amountOut
            )

            is OneInchUnoswapDecoration -> getOneInchSwapViewItems(
                decoration.tokenIn,
                decoration.tokenOut,
                decoration.amountIn,
                decoration.amountOut
            )

            is OneInchDecoration -> null

            is OutgoingEip721Decoration -> getNftTransferItems(
                decoration.to,
                BigInteger.ONE,
                additionalInfo?.sendInfo,
                decoration.tokenId,
            )

            is OutgoingEip1155Decoration -> getNftTransferItems(
                decoration.to,
                decoration.value,
                additionalInfo?.sendInfo,
                decoration.tokenId,
            )

            else -> null
        }

    private fun getNftTransferItems(
        recipient: Address,
        value: BigInteger,
        sendInfo: SendEvmData.SendInfo?,
        tokenId: BigInteger
    ): List<SectionViewItem> {

        val sections = mutableListOf<SectionViewItem>()
        val addressValue = recipient.eip55

        val viewItems = buildList {
            add(
                ViewItem.Subhead(
                    Translator.getString(R.string.Send_Confirmation_YouSend),
                    sendInfo?.nftShortMeta?.nftName ?: tokenId.toString(),
                    R.drawable.ic_arrow_up_right_12
                )
            )

            add(
                getNftAmount(
                    value,
                    sendInfo?.nftShortMeta?.previewImageUrl
                )
            )

            val contact = getContact(addressValue)

            add(
                ViewItem.Address(
                    Translator.getString(R.string.Send_Confirmation_To),
                    addressValue,
                    contact == null,
                    blockchainType,
                    StatSection.AddressTo
                )
            )

            contact?.let {
                add(
                    ViewItem.ContactItem(it)
                )
            }
        }

        sections.add(SectionViewItem(viewItems))

        return sections
    }

    private fun getUniswapViewItems(
        amountIn: SwapDecoration.Amount,
        amountOut: SwapDecoration.Amount,
        tokenIn: SwapDecoration.Token,
        tokenOut: SwapDecoration.Token,
        recipient: Address?
    ): List<SectionViewItem>? {

        val coinServiceIn = getCoinService(tokenIn) ?: return null
        val coinServiceOut = getCoinService(tokenOut) ?: return null

        val sections = mutableListOf<SectionViewItem>()
        val inViewItems = mutableListOf<ViewItem>()
        val outViewItems = mutableListOf<ViewItem>()
        val otherViewItems = mutableListOf<ViewItem>()

        if (recipient != null) {
            val addressValue = recipient.eip55
            val contact = getContact(addressValue)
            otherViewItems.add(
                ViewItem.Address(
                    Translator.getString(R.string.SwapSettings_RecipientAddressTitle),
                    addressValue,
                    contact == null,
                    blockchainType,
                    StatSection.AddressRecipient
                )
            )
            contact?.let {
                otherViewItems.add(
                    ViewItem.ContactItem(it)
                )
            }
        }

        when (amountIn) {
            is SwapDecoration.Amount.Exact -> { // you pay exact
                val amountData = coinServiceIn.amountData(amountIn.value)
                inViewItems.add(getAmount(amountData, ValueType.Outgoing, coinServiceIn.token))
            }

            is SwapDecoration.Amount.Extremum -> { // you pay estimated
                val maxAmountData = coinServiceIn.amountData(amountIn.value)
                inViewItems.add(getMaxAmount(maxAmountData, coinServiceIn.token))
            }
        }

        when (amountOut) {
            is SwapDecoration.Amount.Exact -> { // you get exact
                val amountData = coinServiceOut.amountData(amountOut.value)
                outViewItems.add(
                    getAmount(amountData, ValueType.Incoming, coinServiceOut.token)
                )
            }

            is SwapDecoration.Amount.Extremum -> { // you get estimated
                val guaranteedAmountData = coinServiceOut.amountData(amountOut.value)
                outViewItems.add(getGuaranteedAmount(guaranteedAmountData, coinServiceOut.token))
            }
        }

        inViewItems.add(
            0, ViewItem.Subhead(
                Translator.getString(R.string.Swap_FromAmountTitle),
                coinServiceIn.token.coin.name,
                R.drawable.ic_arrow_up_right_12
            )
        )
        sections.add(SectionViewItem(inViewItems))

        outViewItems.add(
            0, ViewItem.Subhead(
                Translator.getString(R.string.Swap_ToAmountTitle),
                coinServiceOut.token.coin.name,
                R.drawable.ic_arrow_down_left_12
            )
        )
        sections.add(SectionViewItem(outViewItems))

        if (otherViewItems.isNotEmpty()) {
            sections.add(SectionViewItem(otherViewItems))
        }

        return sections
    }

    private fun getOneInchSwapViewItems(
        tokenIn: OneInchDecoration.Token,
        tokenOut: OneInchDecoration.Token?,
        amountIn: BigInteger,
        amountOut: OneInchDecoration.Amount
    ): List<SectionViewItem>? {
        val coinServiceIn = getCoinService(tokenIn) ?: return null
        val coinServiceOut = tokenOut?.let { getCoinService(it) } ?: return null

        val sections = mutableListOf<SectionViewItem>()

        sections.add(
            SectionViewItem(
                listOf(
                    ViewItem.Subhead(
                        Translator.getString(R.string.Swap_FromAmountTitle),
                        coinServiceIn.token.coin.name,
                        R.drawable.ic_arrow_up_right_12
                    ),
                    getAmount(
                        coinServiceIn.amountData(amountIn),
                        ValueType.Outgoing,
                        coinServiceIn.token
                    )
                )
            )
        )

        val outViewItems: MutableList<ViewItem> = mutableListOf(
            ViewItem.Subhead(
                Translator.getString(R.string.Swap_ToAmountTitle),
                coinServiceOut.token.coin.name,
                R.drawable.ic_arrow_down_left_12
            )
        )

        if (amountOut is OneInchDecoration.Amount.Extremum) {
            val guaranteedAmountData = coinServiceOut.amountData(amountOut.value)

            outViewItems.add(getGuaranteedAmount(guaranteedAmountData, coinServiceOut.token))
        }
        sections.add(SectionViewItem(outViewItems))

        return sections
    }

    private fun getEip20TransferViewItems(
        to: Address,
        value: BigInteger,
        contractAddress: Address
    ): List<SectionViewItem>? {
        val coinService = coinServiceFactory.getCoinService(contractAddress) ?: return null

        val viewItems: MutableList<ViewItem> = mutableListOf(
            getAmountWithTitle(
                coinService.amountData(value),
                ValueType.Outgoing,
                coinService.token,
                Translator.getString(R.string.Send_Confirmation_YouSend),
                coinService.token.badge
            )
        )
        val addressValue = to.eip55
        val contact = getContact(addressValue)
        viewItems.add(
            ViewItem.Address(
                Translator.getString(R.string.Send_Confirmation_To),
                addressValue,
                contact == null,
                blockchainType,
                StatSection.AddressTo
            )
        )
        contact?.let {
            viewItems.add(
                ViewItem.ContactItem(it)
            )
        }

        return listOf(SectionViewItem(viewItems))
    }

    private fun getContact(addressValue: String): Contact? {
        return contactsRepo.getContactsFiltered(blockchainType, addressQuery = addressValue).firstOrNull()
    }

    private fun getEip20ApproveViewItems(
        spender: Address,
        value: BigInteger,
        contractAddress: Address
    ): List<SectionViewItem>? {
        val coinService = coinServiceFactory.getCoinService(contractAddress) ?: return null

        val viewItems = buildList {
            if (value.compareTo(BigInteger.ZERO) == 0) {
                add(
                    ViewItem.Subhead(
                        Translator.getString(R.string.Approve_YouRevoke),
                        coinService.token.coin.name,
                    )
                )
                add(ViewItem.TokenItem(coinService.token))
            } else {
                add(
                    getAmountWithTitle(
                        coinService.amountData(value),
                        ValueType.Regular,
                        coinService.token,
                        Translator.getString(R.string.Approve_YouApprove),
                        coinService.token.badge
                    )
                )
            }

            val addressValue = spender.eip55
            val contact = getContact(addressValue)
            add(
                ViewItem.Address(
                    Translator.getString(R.string.Approve_Spender),
                    addressValue,
                    contact == null,
                    blockchainType,
                    StatSection.AddressSpender
                )
            )
            contact?.let {
                add(
                    ViewItem.ContactItem(it)
                )
            }
        }

        return listOf(SectionViewItem(viewItems))
    }

    private fun getWalletConnectSectionView(
        info: SendEvmData.WalletConnectInfo
    ) = SectionViewItem(
        buildList {
            info.dAppName?.let {
                add(
                    ViewItem.Value(
                        Translator.getString(R.string.WalletConnect_SignMessageRequest_dApp),
                        it,
                        ValueType.Regular
                    )
                )
            }
            info.chain?.let {
                add(
                    ViewItem.Value(
                        it.chain.name,
                        it.address ?: "",
                        ValueType.Regular
                    )
                )
            }
        }
    )

    private fun getUnknownMethodItems(
        transactionData: TransactionData,
        methodName: String?,
    ): List<SectionViewItem> {
        val viewItems = buildList {
            add(
                getAmount(
                    coinServiceFactory.baseCoinService.amountData(transactionData.value),
                    ValueType.Outgoing,
                    coinServiceFactory.baseCoinService.token
                )
            )
            val toValue = transactionData.to.eip55
            val contact = getContact(toValue)
            add(
                ViewItem.Address(
                    Translator.getString(R.string.Send_Confirmation_To),
                    toValue,
                    contact == null,
                    blockchainType,
                    StatSection.AddressTo
                )
            )
            contact?.let {
                add(
                    ViewItem.ContactItem(it)
                )
            }

            methodName?.let {
                add(ViewItem.Value(Translator.getString(R.string.Send_Confirmation_Method), it, ValueType.Regular))
            }

            add(ViewItem.Input(transactionData.input.toHexString()))
        }

        return listOf(SectionViewItem(viewItems))
    }

    private fun getSendBaseCoinItems(to: Address, value: BigInteger): List<SectionViewItem> {
        val toValue = to.eip55
        val baseCoinService = coinServiceFactory.baseCoinService

        val viewItems = buildList {
            add(
                getAmountWithTitle(
                    baseCoinService.amountData(value),
                    ValueType.Outgoing,
                    baseCoinService.token,
                    Translator.getString(R.string.Send_Confirmation_YouSend),
                    baseCoinService.token.badge,
                )
            )
            val contact = getContact(toValue)
            add(
                ViewItem.Address(
                    Translator.getString(R.string.Send_Confirmation_To),
                    toValue,
                    contact == null,
                    blockchainType,
                    StatSection.AddressTo
                )
            )
            contact?.let {
                add(
                    ViewItem.ContactItem(it)
                )
            }
        }

        return listOf(
            SectionViewItem(
                viewItems
            )
        )
    }

    private fun getCoinService(token: SwapDecoration.Token) = when (token) {
        SwapDecoration.Token.EvmCoin -> coinServiceFactory.baseCoinService
        is SwapDecoration.Token.Eip20Coin -> coinServiceFactory.getCoinService(token.address)
    }

    private fun getCoinService(token: OneInchDecoration.Token) = when (token) {
        OneInchDecoration.Token.EvmCoin -> coinServiceFactory.baseCoinService
        is OneInchDecoration.Token.Eip20Coin -> coinServiceFactory.getCoinService(token.address)
    }

    private fun getNftAmount(value: BigInteger, previewImageUrl: String?): ViewItem.NftAmount =
        ViewItem.NftAmount(previewImageUrl, "$value NFT", ValueType.Regular)

    private fun getAmount(amountData: SendModule.AmountData, valueType: ValueType, token: Token) =
        ViewItem.Amount(
            amountData.secondary?.getFormatted(),
            amountData.primary.getFormatted(),
            valueType,
            token
        )

    private fun getAmountWithTitle(
        amountData: SendModule.AmountData,
        valueType: ValueType,
        token: Token,
        title: String,
        badge: String?
    ) =
        ViewItem.AmountWithTitle(
            amountData.secondary?.getFormatted(),
            amountData.primary.getFormatted(),
            valueType,
            token,
            title,
            badge
        )

    private fun getGuaranteedAmount(amountData: SendModule.AmountData, token: Token) =
        ViewItem.Amount(
            amountData.secondary?.getFormatted(),
            "${amountData.primary.getFormatted()} ${Translator.getString(R.string.Swap_AmountMin)}",
            ValueType.Incoming,
            token
        )

    private fun getMaxAmount(amountData: SendModule.AmountData, token: Token) =
        ViewItem.Amount(
            amountData.secondary?.getFormatted(),
            "${amountData.primary.getFormatted()} ${Translator.getString(R.string.Swap_AmountMax)}",
            ValueType.Outgoing,
            token
        )

    private fun convertError(error: Throwable) =
        when (val convertedError = error.convertedError) {
            is SendEvmTransactionService.TransactionError.InsufficientBalance -> {
                Translator.getString(
                    R.string.EthereumTransaction_Error_InsufficientBalance,
                    coinServiceFactory.baseCoinService.coinValue(convertedError.requiredBalance)
                        .getFormattedFull()
                )
            }
            is EvmError.InsufficientBalanceWithFee -> {
                Translator.getString(
                    R.string.EthereumTransaction_Error_InsufficientBalanceForFee,
                    coinServiceFactory.baseCoinService.token.coin.code
                )
            }
            is EvmError.ExecutionReverted -> {
                Translator.getString(
                    R.string.EthereumTransaction_Error_ExecutionReverted,
                    convertedError.message ?: ""
                )
            }
            is EvmError.CannotEstimateSwap -> {
                Translator.getString(
                    R.string.EthereumTransaction_Error_CannotEstimate,
                    coinServiceFactory.baseCoinService.token.coin.code
                )
            }
            is EvmError.LowerThanBaseGasLimit -> Translator.getString(R.string.EthereumTransaction_Error_LowerThanBaseGasLimit)
            is EvmError.InsufficientLiquidity -> Translator.getString(R.string.EthereumTransaction_Error_InsufficientLiquidity)
            else -> convertedError.message ?: convertedError.javaClass.simpleName
        }

}

data class SectionViewItem(
    val viewItems: List<ViewItem>
)

sealed class ViewItem {
    class Subhead(val title: String, val value: String, val iconRes: Int? = null) : ViewItem()
    class Value(
        val title: String,
        val value: String,
        val type: ValueType,
    ) : ViewItem()

    class ValueMulti(
        val title: String,
        val primaryValue: String,
        val secondaryValue: String,
        val type: ValueType,
    ) : ViewItem()

    class AmountMulti(
        val amounts: List<AmountValues>,
        val type: ValueType,
        val token: Token
    ) : ViewItem()

    class Amount(
        val fiatAmount: String?,
        val coinAmount: String,
        val type: ValueType,
        val token: Token
    ) : ViewItem()

    class AmountWithTitle(
        val fiatAmount: String?,
        val coinAmount: String,
        val type: ValueType,
        val token: Token,
        val title: String,
        val badge: String?
    ) : ViewItem()

    class NftAmount(
        val iconUrl: String?,
        val amount: String,
        val type: ValueType,
    ) : ViewItem()

    class Address(val title: String, val value: String, val showAdd: Boolean, val blockchainType: BlockchainType, val statSection: StatSection) : ViewItem()
    class Input(val value: String) : ViewItem()
    class TokenItem(val token: Token) : ViewItem()
    class ContactItem(val contact: Contact) : ViewItem()
}

data class AmountValues(val coinAmount: String, val fiatAmount: String?)

enum class ValueType {
    Regular, Disabled, Outgoing, Incoming, Warning, Forbidden
}
