package io.horizontalsystems.bankwallet.modules.market.platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coin.chain.crypto.core.designsystem.component.TopAppBarClose
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseFragment
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.chart.ChartViewModel
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Chart
import io.horizontalsystems.bankwallet.modules.coin.overview.ui.Loading
import io.horizontalsystems.bankwallet.modules.market.ImageSource
import io.horizontalsystems.bankwallet.modules.market.topcoins.SelectorDialogState
import io.horizontalsystems.bankwallet.modules.market.topplatforms.Platform
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.components.AlertGroup
import io.horizontalsystems.bankwallet.ui.compose.components.ButtonSecondaryToggle
import io.horizontalsystems.bankwallet.ui.compose.components.CoinList
import io.horizontalsystems.bankwallet.ui.compose.components.HeaderSorting
import io.horizontalsystems.bankwallet.ui.compose.components.ListErrorView
import io.horizontalsystems.bankwallet.ui.compose.components.SortMenu
import io.horizontalsystems.bankwallet.ui.compose.components.subhead2_grey
import io.horizontalsystems.bankwallet.ui.compose.components.title3_leah
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.parcelable

class MarketPlatformFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val platformUid = activity?.intent?.data?.getQueryParameter("uid")
        val platformTitle = activity?.intent?.data?.getQueryParameter("title")

        val platform = if (platformUid != null && platformTitle != null) {
            Platform(platformUid, platformTitle)
        } else {
            arguments?.parcelable(platformKey)
        }

        if (platform == null) {
            findNavController().popBackStack()
            return null
        }

        val factory = MarketPlatformModule.Factory(platform)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                ComposeAppTheme {
                    PlatformScreen(
                        factory = factory,
                        onCloseButtonClick = { findNavController().popBackStack() },
                        onCoinClick = { coinUid ->
                            val arguments = CoinFragment.prepareParams(coinUid)
                            findNavController().slideFromRight(R.id.coinFragment, arguments)
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val platformKey = "platform_key"

        fun prepareParams(platform: Platform): Bundle {
            return bundleOf(platformKey to platform)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlatformScreen(
    factory: ViewModelProvider.Factory,
    onCloseButtonClick: () -> Unit,
    onCoinClick: (String) -> Unit,
    viewModel: MarketPlatformViewModel = viewModel(factory = factory),
    chartViewModel: ChartViewModel = viewModel(factory = factory),
) {

    var scrollToTopAfterUpdate by rememberSaveable { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Surface(color = Color.Transparent) {
        Column {
            TopAppBarClose(
                actionIcon = Icons.Rounded.Close,
                actionIconContentDescription = "ArrowBack",
                onActionClick = onCloseButtonClick,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                )
            )

            HSSwipeRefresh(
                refreshing = viewModel.isRefreshing,
                onRefresh = {
                    viewModel.refresh()
                }
            ) {
                Crossfade(viewModel.viewState, label = "") { state ->
                    when (state) {
                        ViewState.Loading -> {
                            Loading()
                        }

                        is ViewState.Error -> {
                            ListErrorView(
                                stringResource(R.string.SyncError),
                                viewModel::onErrorClick
                            )
                        }

                        ViewState.Success -> {
                            viewModel.viewItems.let { viewItems ->
                                CoinList(
                                    items = viewItems,
                                    scrollToTop = scrollToTopAfterUpdate,
                                    onAddFavorite = { uid ->
                                        viewModel.onAddFavorite(uid)
                                    },
                                    onRemoveFavorite = { uid ->
                                        viewModel.onRemoveFavorite(uid)
                                    },
                                    onCoinClick = onCoinClick,
                                    preItems = {
                                        viewModel.header.let {
                                            item {
                                                HeaderContent(it.title, it.description, it.icon)
                                            }
                                        }
                                        item {
                                            Chart(chartViewModel = chartViewModel)
                                        }
                                        stickyHeader {
                                            HeaderSorting(borderTop = true, borderBottom = true) {
                                                Box(modifier = Modifier.weight(1f)) {
                                                    SortMenu(
                                                        viewModel.menu.sortingFieldSelect.selected.titleResId,
                                                        viewModel::showSelectorMenu
                                                    )
                                                }
                                                Box(
                                                    modifier = Modifier.padding(
                                                        start = 8.dp,
                                                        end = 16.dp
                                                    )
                                                ) {
                                                    ButtonSecondaryToggle(
                                                        select = viewModel.menu.marketFieldSelect,
                                                        onSelect = viewModel::onSelectMarketField
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                                if (scrollToTopAfterUpdate) {
                                    scrollToTopAfterUpdate = false
                                }
                            }
                        }
                    }
                }
            }
        }
        //Dialog
        when (val option = viewModel.selectorDialogState) {
            is SelectorDialogState.Opened -> {
                AlertGroup(
                    R.string.Market_Sort_PopupTitle,
                    option.select,
                    { selected ->
                        viewModel.onSelectSortingField(selected)
                        scrollToTopAfterUpdate = true
                    },
                    { viewModel.onSelectorDialogDismiss() }
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun HeaderContent(title: String, description: String, image: ImageSource) {
    Column {
        Row(
            modifier = Modifier
                .height(100.dp)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 12.dp, end = 8.dp)
                    .weight(1f)
            ) {
                title3_leah(
                    text = title,
                )
                subhead2_grey(
                    text = description,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Image(
                    painter = image.painter(),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun HeaderContentPreview() {
    ComposeAppTheme {
        HeaderContent(
            "Solana Ecosystem",
            "Market cap of all protocols on the Solana chain",
            ImageSource.Local(R.drawable.logo_ethereum_24)
        )
    }
}
