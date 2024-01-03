@file:Suppress("ModifierReused")

package io.horizontalsystems.bankwallet.ui.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.billing.models.ScreenState
import io.horizontalsystems.bankwallet.ui.compose.components.ErrorView
import io.horizontalsystems.bankwallet.ui.compose.components.LoadingView

@Composable
fun <T> AsyncLoadContents(
    screenState: ScreenState<T>,
    modifier: Modifier = Modifier,
    otherModifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    cornerShape: RoundedCornerShape = RoundedCornerShape(0.dp),
    retryAction: () -> Unit = {},
    content: @Composable (T) -> Unit,
) {
    AnimatedContent(
        modifier = modifier
            .clip(cornerShape)
            .background(containerColor),
        targetState = screenState,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
        contentKey = { it.javaClass },
        label = "AsyncLoadContents",
    ) { state ->
        when (state) {
            is ScreenState.Idle -> {
                content.invoke(state.data)
            }
            is ScreenState.Loading -> {
                LoadingView(
                    modifier = otherModifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.2f)),
                )
            }
            is ScreenState.Error -> {
                ErrorView(
                    modifier = otherModifier.fillMaxWidth(),
                    errorState = state,
                    retryAction = retryAction,
                )
            }
        }
    }
}

@Composable
fun <T> AsyncLoadContentsWithoutAnimation(
    screenState: ScreenState<T>,
    modifier: Modifier = Modifier,
    otherModifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    cornerShape: RoundedCornerShape = RoundedCornerShape(0.dp),
    retryAction: () -> Unit = {},
    content: @Composable (T) -> Unit,
) {
    Box(
        modifier = modifier
            .clip(cornerShape)
            .background(containerColor),
    ) {
        when (screenState) {
            is ScreenState.Idle -> {
                content.invoke(screenState.data)
            }
            is ScreenState.Loading -> {
                LoadingView(
                    modifier = otherModifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.2f)),
                )
            }
            is ScreenState.Error -> {
                ErrorView(
                    modifier = otherModifier.fillMaxWidth(),
                    errorState = screenState,
                    retryAction = retryAction,
                )
            }
        }
    }
}

@Composable
fun <T> AsyncNoLoadContents(
    screenState: ScreenState<T>,
    modifier: Modifier = Modifier,
    otherModifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    cornerShape: RoundedCornerShape = RoundedCornerShape(0.dp),
    retryAction: () -> Unit = {},
    content: @Composable (T?) -> Unit,
) {
    Box(
        modifier = modifier
            .clip(cornerShape)
            .background(containerColor),
    ) {
        when (screenState) {
            is ScreenState.Idle, is ScreenState.Loading -> {
                content.invoke((screenState as? ScreenState.Idle)?.data)
            }
            is ScreenState.Error -> {
                ErrorView(
                    modifier = otherModifier.fillMaxWidth(),
                    errorState = screenState,
                    retryAction = retryAction,
                )
            }
        }
    }
}
