package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.horizontalsystems.bankwallet.modules.market.ImageSource
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme

data class TabItem<T>(val title: String, val selected: Boolean, val item: T, val icon: ImageSource? = null, val label: String? = null, val enabled: Boolean = true)

@Composable
fun <T>Tabs(tabs: List<TabItem<T>>, onClick: (T) -> Unit) {
    val selectedIndex = tabs.indexOfFirst { it.selected }

    Box(
        modifier = Modifier
            .height(50.dp)
            .background(Color.Transparent)
    ) {

        TabRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(Color.Transparent)
                .height(48.dp),
            selectedTabIndex = selectedIndex,
            contentColor = Color.Transparent,
            containerColor = Color.Transparent,
            indicator = @Composable { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {
                Divider(
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp),
                    selected = tab.selected,
                    onClick = {
                        onClick.invoke(tab.item)
                    },
                    content = {
                        ProvideTextStyle(
                            MaterialTheme.typography.titleSmall
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = 6.dp),
                                text = tab.title,
                                color = if (selectedIndex == index) MaterialTheme.colorScheme.primary else Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    })
            }
        }
    }
}

@Composable
fun <T>ScrollableTabs(tabs: List<TabItem<T>>, onClick: (T) -> Unit) {
    val selectedIndex = tabs.indexOfFirst { it.selected }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onBackground)
            .height(44.dp)
    ) {
        Divider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = 1.dp,
        )

        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            contentColor = MaterialTheme.colorScheme.onSurface,
            edgePadding = 16.dp,
            indicator = @Composable { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEach { tab ->
                Tab(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 12.dp),
                    selected = tab.selected,
                    onClick = {
                        onClick.invoke(tab.item)
                    },
                    content = {
                        ProvideTextStyle(
                            MaterialTheme.typography.bodySmall
                        ) {
                            Text(
                                text = tab.title,
                                color = if (tab.selected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                )
            }
        }
    }
}
