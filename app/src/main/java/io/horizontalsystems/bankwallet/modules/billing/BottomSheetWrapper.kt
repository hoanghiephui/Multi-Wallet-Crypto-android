package io.horizontalsystems.bankwallet.modules.billing

import android.app.Activity
import android.graphics.Rect
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.LocalSystemBars
import io.horizontalsystems.bankwallet.ui.compose.SystemBars
import io.horizontalsystems.bankwallet.ui.compose.components.NiaBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetWrapper(
    parent: ViewGroup,
    composeView: ComposeView,
    skipPartiallyExpanded: Boolean,
    rectCorner: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(skipPartiallyExpanded)
    var isOpen by rememberSaveable { mutableStateOf(true) }
    val cornerRadius = animateDpAsState(
        targetValue = if (state.targetValue != SheetValue.Hidden && !rectCorner) 16.dp else 0.dp,
        label = "cornerRadius",
    )

    LaunchedEffect(isOpen) {
        if (!isOpen) {
            delay(100)
            parent.removeView(composeView)
        }
    }

    if (isOpen) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = state,
            dragHandle = { },
            containerColor = MaterialTheme.colorScheme.surface,
            contentWindowInsets = {
                WindowInsets(0, 0, 0, 0)
            },
            shape = RoundedCornerShape(
                topStart = cornerRadius.value,
                topEnd = cornerRadius.value,
            ),
            onDismissRequest = { isOpen = false },
        ) {
            BackHandler {
                scope.launch {
                    state.hide()
                    isOpen = false
                }
            }

            content {
                scope.launch {
                    state.hide()
                    isOpen = false
                }
            }
        }
    }
}

fun Activity.showAsButtonSheet(
    skipPartiallyExpanded: Boolean = true,
    rectCorner: Boolean = false,
    content: @Composable (() -> Unit) -> Unit,
) {
    val viewGroup = findViewById<ViewGroup>(android.R.id.content)
    val view = ComposeView(viewGroup.context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val density = LocalDensity.current

            val systemBars = this@showAsButtonSheet.systemBarsInsets().let {
                with(density) {
                    SystemBars(
                        top = it.first.toDp(),
                        bottom = it.second.toDp(),
                    )
                }
            }

            ComposeAppTheme {
                NiaBackground(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CompositionLocalProvider(
                        LocalSystemBars provides systemBars,
                        androidx.lifecycle.compose.LocalLifecycleOwner provides androidx.compose.ui.platform.LocalLifecycleOwner.current
                    ) {
                        BottomSheetWrapper(
                            parent = viewGroup,
                            composeView = this,
                            skipPartiallyExpanded = skipPartiallyExpanded,
                            rectCorner = rectCorner,
                            content = content,
                        )
                    }
                }
            }
        }
    }

    viewGroup.addView(view)
}

fun Activity.systemBarsInsets(): Pair<Int, Int> {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val insets =
            windowManager.currentWindowMetrics.windowInsets.getInsetsIgnoringVisibility(android.view.WindowInsets.Type.systemBars())
        insets.top to insets.bottom
    } else {
        val rect = Rect()
        this.window.decorView.getWindowVisibleDisplayFrame(rect)
        rect.top to (this.window.decorView.height - rect.bottom)
    }
}
