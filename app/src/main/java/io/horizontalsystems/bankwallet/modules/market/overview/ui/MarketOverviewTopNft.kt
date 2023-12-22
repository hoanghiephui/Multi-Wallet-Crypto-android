package io.horizontalsystems.bankwallet.modules.market.overview.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.modules.market.MarketDataValue
import io.horizontalsystems.bankwallet.modules.market.TimeDuration
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewModule
import io.horizontalsystems.bankwallet.modules.market.topnftcollections.TopNftCollectionViewItem
import io.horizontalsystems.bankwallet.ui.compose.components.MarketCoinFirstRow
import io.horizontalsystems.bankwallet.ui.compose.components.MarketCoinSecondRow
import io.horizontalsystems.bankwallet.ui.compose.components.NftIcon
import io.horizontalsystems.bankwallet.ui.compose.components.SectionItemBorderedRowUniversalClear
import io.horizontalsystems.marketkit.models.BlockchainType

@Composable
fun TopNftCollectionsBoardView(
    board: MarketOverviewModule.TopNftCollectionsBoard,
    onSelectTimeDuration: (TimeDuration) -> Unit,
    onClickCollection: (BlockchainType, String) -> Unit,
    onClickSeeAll: () -> Unit
) {
    TopBoardHeader(
        title = board.title,
        iconRes = board.iconRes,
        select = board.timeDurationSelect,
        onSelect = onSelectTimeDuration,
        onClickSeeAll = onClickSeeAll
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        board.collections.forEach { collection ->
            TopNftCollectionView(collection) {
                onClickCollection(collection.blockchainType, collection.uid)
            }
        }

        SeeAllButton(onClickSeeAll)
    }

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun TopNftCollectionView(
    collection: TopNftCollectionViewItem,
    onClick: () -> Unit
) {
    SectionItemBorderedRowUniversalClear(
        onClick = onClick,
        borderBottom = true
    ) {
        NftIcon(
            iconUrl = collection.imageUrl ?: "",
            placeholder = R.drawable.coin_placeholder,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            MarketCoinFirstRow(collection.name, collection.volume)
            Spacer(modifier = Modifier.height(3.dp))
            MarketCoinSecondRow(
                collection.floorPrice,
                MarketDataValue.Diff(collection.volumeDiff),
                "${collection.order}"
            )
        }
    }
}
