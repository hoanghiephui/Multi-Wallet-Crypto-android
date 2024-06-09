package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.horizontalsystems.bankwallet.modules.market.ImageSource
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme

data class TabItem<T>(
    val title: String,
    val selected: Boolean,
    val item: T,
    val icon: ImageSource? = null,
    val label: String? = null,
    val enabled: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Tabs(tabs: List<TabItem<T>>, onClick: (T) -> Unit) {
    val selectedIndex = tabs.indexOfFirst { it.selected }

    PrimaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                selected = tab.selected,
                onClick = {
                    onClick.invoke(tab.item)
                },
                content = {
                    ProvideTextStyle(
                        ComposeAppTheme.typography.subhead1
                    ) {
                        Text(
                            text = tab.title,
                            color = if (selectedIndex == index) ComposeAppTheme.colors.leah else ComposeAppTheme.colors.grey,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ScrollableTabs(tabs: List<TabItem<T>>, onClick: (T) -> Unit) {
    val selectedIndex = tabs.indexOfFirst { it.selected }
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        edgePadding = 16.dp,
    ) {
        tabs.forEach { tab ->
            Tab(
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = 12.dp),
                selected = tab.selected,
                onClick = {
                    onClick.invoke(tab.item)
                },
                content = {
                    ProvideTextStyle(
                        ComposeAppTheme.typography.subhead1
                    ) {
                        Text(
                            text = tab.title,
                            color = if (tab.selected) ComposeAppTheme.colors.leah else ComposeAppTheme.colors.grey
                        )
                    }
                }
            )
        }
    }
}
