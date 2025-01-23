package io.horizontalsystems.bankwallet.ui

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.ranges.coerceIn

class CollapsingAppBarNestedScrollConnection : NestedScrollConnection {

    var headerOffset: Float by mutableFloatStateOf(0f)
        private set
    var progress: Float by mutableFloatStateOf(1f)
        private set

    var maxHeight: Float by mutableFloatStateOf(0f)
    var minHeight: Float by mutableFloatStateOf(0f)

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        /**
         *  when direction is negative, meaning scrolling downward,
         *  we are not consuming delta but passing it for Node Consumption
         */
        if (delta >= 0f) {
            return Offset.Zero
        }
        val newOffset = headerOffset + delta
        val previousOffset = headerOffset
        val heightDelta = -(maxHeight - minHeight)
        headerOffset = if (heightDelta > 0) 0f else newOffset.coerceIn(heightDelta, 0f)
        progress = 1f - headerOffset / -maxHeight
        val consumed = headerOffset - previousOffset
        return Offset(0f, consumed)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y
        val newOffset = headerOffset + delta
        val previousOffset = headerOffset
        val heightDelta = -(maxHeight - minHeight)
        headerOffset = if (heightDelta > 0) 0f else newOffset.coerceIn(heightDelta, 0f)
        progress = 1f - headerOffset / -maxHeight
        val consumedValue = headerOffset - previousOffset
        return Offset(0f, consumedValue)
    }
}

@Composable
fun CollapsingLayout(
    modifier: Modifier = Modifier,
    expandedContent: @Composable (Modifier) -> Unit,
    collapsedContent: @Composable (Modifier) -> Unit,
    content: @Composable ColumnScope.(Modifier) -> Unit
) {
    val localDensity = LocalDensity.current
    var currentHeight by remember { mutableFloatStateOf(0f) }
    var maxHeight by remember { mutableFloatStateOf(-1f) }
    var minHeight by remember { mutableFloatStateOf(-1f) }

    val animatedHeight by animateFloatAsState(
        targetValue = currentHeight,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
        label = "CollapsingHeight"
    )

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Chỉ xử lý cuộn nếu chưa ở trạng thái minHeight (tránh xung đột SwipeRefresh)
                return if (currentHeight > minHeight && available.y < 0) {
                    currentHeight = (currentHeight + available.y).coerceAtLeast(minHeight)
                    available
                } else Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // Chỉ xử lý mở rộng nếu chưa ở maxHeight (tránh xung đột SwipeRefresh)
                return if (currentHeight < maxHeight && available.y > 0) {
                    currentHeight = (currentHeight + available.y).coerceAtMost(maxHeight)
                    available
                } else Offset.Zero
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .nestedScroll(nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    val height = coordinates.size.height.toFloat()
                    if (maxHeight == -1f) {
                        maxHeight = height
                        currentHeight = height
                    }
                }
                .then(
                    if (maxHeight > 0f) {
                        Modifier.height(with(localDensity) { animatedHeight.toDp() })
                    } else {
                        Modifier
                    }
                )
                .clipToBounds()
        ) {
            expandedContent(
                Modifier.onGloballyPositioned { coordinates ->
                    if (minHeight == -1f) {
                        minHeight = coordinates.size.height.toFloat()
                    }
                }
            )
            collapsedContent(Modifier)
        }

        content(Modifier.weight(1f))
    }
}


