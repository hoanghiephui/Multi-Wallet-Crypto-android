package io.horizontalsystems.bankwallet.modules.btcblockchainsettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.imageUrl
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.entities.BtcRestoreMode
import io.horizontalsystems.bankwallet.modules.btcblockchainsettings.BtcBlockchainSettingsModule.ViewItem
import io.horizontalsystems.marketkit.models.Blockchain
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class BtcBlockchainSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val blockchain: Blockchain = checkNotNull(savedStateHandle["blockchain"])
    private val service: BtcBlockchainSettingsService = BtcBlockchainSettingsService(
        blockchain,
        App.btcBlockchainManager
    )
    private val disposables = CompositeDisposable()

    var closeScreen by mutableStateOf(false)
        private set

    var restoreSources by mutableStateOf<List<ViewItem>>(listOf())
        private set

    var saveButtonEnabled by mutableStateOf(false)
        private set

    val title: String = service.blockchain.name
    val blockchainIconUrl = service.blockchain.type.imageUrl

    init {
        service.hasChangesObservable
            .subscribe {
                saveButtonEnabled = it
                syncRestoreModeState()
            }.let {
                disposables.add(it)
            }

        syncRestoreModeState()
    }

    override fun onCleared() {
        disposables.clear()
    }

    fun onSelectRestoreMode(viewItem: ViewItem) {
        service.setRestoreMode(viewItem.id)
    }

    fun onSaveClick() {
        service.save()
        closeScreen = true
    }

    private fun syncRestoreModeState() {
        val viewItems = BtcRestoreMode.entries.map { mode ->
            ViewItem(
                id = mode.raw,
                title = Translator.getString(mode.title),
                subtitle = Translator.getString(mode.description),
                selected = mode == service.restoreMode
            )
        }
        restoreSources = viewItems
    }

}
