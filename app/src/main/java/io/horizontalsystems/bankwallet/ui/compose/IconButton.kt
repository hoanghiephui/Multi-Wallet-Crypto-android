package io.horizontalsystems.bankwallet.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.horizontalsystems.bankwallet.ui.compose.components.ThemePreviews

/**
 * Now in Android toggle button with icon and checked icon content slots. Wraps Material 3
 * [IconButton].
 *
 * @param checked Whether the toggle button is currently checked.
 * @param onCheckedChange Called when the user clicks the toggle button and toggles checked.
 * @param modifier Modifier to be applied to the toggle button.
 * @param enabled Controls the enabled state of the toggle button. When `false`, this toggle button
 * will not be clickable and will appear disabled to accessibility services.
 * @param icon The icon content to show when unchecked.
 * @param checkedIcon The icon content to show when checked.
 */
@Composable
fun NiaIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    checkedIcon: @Composable () -> Unit = icon,
) {
    // TODO: File bug
    // Can't use regular IconToggleButton as it doesn't include a shape (appears square)
    FilledIconToggleButton(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconToggleButtonColors(
            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = if (checked) {
                MaterialTheme.colorScheme.onBackground.copy(
                    alpha = NiaIconButtonDefaults.DISABLED_ICON_BUTTON_CONTAINER_ALPHA,
                )
            } else {
                Color.Transparent
            },
        ),
    ) {
        if (checked) checkedIcon() else icon()
    }
}

@ThemePreviews
@Composable
fun IconButtonPreview() {
    ComposeAppTheme {
        NiaIconToggleButton(
            checked = true,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                )
            },
        )
    }
}

@ThemePreviews
@Composable
fun IconButtonPreviewUnchecked() {
    ComposeAppTheme {
        NiaIconToggleButton(
            checked = false,
            onCheckedChange = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                )
            },
            checkedIcon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                )
            },
        )
    }
}

/**
 * Now in Android icon button default values.
 */
object NiaIconButtonDefaults {
    // TODO: File bug
    // IconToggleButton disabled container alpha not exposed by IconButtonDefaults
    const val DISABLED_ICON_BUTTON_CONTAINER_ALPHA = 0.12f
}