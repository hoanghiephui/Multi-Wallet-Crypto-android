package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString

data class MenuItem(
    val title: TranslatableString,
    @DrawableRes val icon: Int? = null,
    val enabled: Boolean = true,
    val tint: Color = Color.Unspecified,
    val onClick: () -> Unit,
)

@Composable
fun AppBarMenuButton(
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    description: String,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    HsIconButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = icon),
            contentDescription = description,
            tint = tint
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    menuItems: List<MenuItem> = listOf(),
    showSpinner: Boolean = false,
    backgroundColor: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
) {
    val titleComposable: @Composable () -> Unit = {
        title?.let {
            title3_leah(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    AppBar(
        title = titleComposable,
        navigationIcon = navigationIcon,
        menuItems = menuItems,
        showSpinner = showSpinner,
        backgroundColor = backgroundColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    menuItems: List<MenuItem> = listOf(),
    showSpinner: Boolean = false,
    backgroundColor: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
) {
    TopAppBar(
        title = title,
        colors = backgroundColor,
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = {
            if (showSpinner) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 16.dp)
                        .size(24.dp),
                    color = ComposeAppTheme.colors.grey,
                    strokeWidth = 2.dp
                )
            }
            menuItems.forEach { menuItem ->
                val color = if (menuItem.enabled) {
                    if (menuItem.tint == Color.Unspecified)
                        MaterialTheme.colorScheme.onSurface
                    else
                        menuItem.tint
                } else {
                    ComposeAppTheme.colors.grey50
                }

                if (menuItem.icon != null) {
                    AppBarMenuButton(
                        icon = menuItem.icon,
                        onClick = menuItem.onClick,
                        enabled = menuItem.enabled,
                        tint = color,
                        description = menuItem.title.getString()
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable(
                                enabled = menuItem.enabled,
                                onClick = menuItem.onClick
                            ),
                        text = menuItem.title.getString().toUpperCase(Locale.current),
                        style = ComposeAppTheme.typography.headline2,
                        color = color
                    )
                }
            }
        },
    )
}
