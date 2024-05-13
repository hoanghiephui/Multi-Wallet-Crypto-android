package io.horizontalsystems.bankwallet.modules.market.overview.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import androidx.navigation.NavController
import com.wallet.blockchain.bitcoin.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.stats.StatEvent
import io.horizontalsystems.bankwallet.core.stats.StatPage
import io.horizontalsystems.bankwallet.core.stats.stat
import io.horizontalsystems.bankwallet.core.stats.statPage
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewModule
import io.horizontalsystems.bankwallet.modules.metricchart.MetricsType
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.caption_grey
import io.horizontalsystems.bankwallet.ui.extensions.MetricData
import io.horizontalsystems.chartview.ChartMinimal
import java.math.BigDecimal

@Composable
fun MetricChartsView(
    marketMetrics: MarketOverviewModule.MarketMetrics,
    navController: NavController
) {
    val items = listOf(
        marketMetrics.totalMarketCap,
        marketMetrics.volume24h,
        marketMetrics.defiCap,
        marketMetrics.defiTvl
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        content = {
            items(items) {
                ChartView(it, navController)
            }
        }
    )
}

@Composable
private fun ChartView(metricsData: MetricData, navController: NavController) {
    Card(
        modifier = Modifier
            .height(105.dp)
            .width(200.dp)
            .clickable {
                openMetricsPage(metricsData.type, navController)
            },
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            caption_grey(text = stringResource(metricsData.type.title))
            Spacer(modifier = Modifier.height(10.dp))
            if (metricsData.value != null) {
                Text(
                    text = metricsData.value,
                    style = ComposeAppTheme.typography.headline1,
                    color = ComposeAppTheme.colors.bran,
                )
            } else {
                Text(
                    text = stringResource(R.string.NotAvailable),
                    style = ComposeAppTheme.typography.headline1,
                    color = ComposeAppTheme.colors.grey50,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.Bottom) {
                if (metricsData.diff != null) {
                    val sign = if (metricsData.diff >= BigDecimal.ZERO) "+" else "-"
                    Text(
                        text = App.numberFormatter.format(metricsData.diff.abs(), 0, 2, sign, "%"),
                        style = ComposeAppTheme.typography.subhead1,
                        color = if (metricsData.diff >= BigDecimal.ZERO) ComposeAppTheme.colors.remus else ComposeAppTheme.colors.lucian,
                    )
                } else {
                    Text(
                        text = "----",
                        style = ComposeAppTheme.typography.subhead1,
                        color = ComposeAppTheme.colors.grey50,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                metricsData.chartData?.let { chartData ->
                    AndroidView(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 3.dp, bottom = 6.dp)
                            .height(24.dp),
                        factory = {
                            ChartMinimal(it)
                        },
                        update = { view ->
                            view.doOnLayout {
                                view.setData(chartData)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

private fun openMetricsPage(metricsType: MetricsType, navController: NavController) {
    if (metricsType == MetricsType.TvlInDefi) {
        navController.slideFromBottom(R.id.tvlFragment)
    } else {
        navController.slideFromBottom(R.id.metricsPageFragment, metricsType)
    }

    stat(page = StatPage.MarketOverview, event = StatEvent.Open(metricsType.statPage))
}
