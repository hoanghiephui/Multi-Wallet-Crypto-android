package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    content: @Composable (() -> Unit),
    navigationAction: () -> Unit,
    menuItems: List<MenuItem> = listOf(),
    hint: String,
    onSearchTextChanged: (String) -> Unit = {},
    title: String
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    var searchMode by remember { mutableStateOf(false) }
    var searchClear by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val menuItem = mutableListOf(
        MenuItem(
            title = TranslatableString.ResString(R.string.Button_Search),
            icon = R.drawable.icon_search,
            onClick = {
                searchMode = true
                active = true
                keyboardController?.show()
            },
            tint = MaterialTheme.colorScheme.onSurface
        )
    ).also {
        it.addAll(menuItems)
    }
    Box(
        Modifier
            .fillMaxSize()
    ) {
        if (!searchMode) {
            Column {
                AppBar(
                    title = title,
                    navigationIcon = {
                        HsBackButton(onClick = navigationAction)
                    },
                    menuItems = menuItem
                )
                content.invoke()
            }
        } else {
            androidx.compose.material3.SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
                query = text,
                onQueryChange = {
                    text = it
                    onSearchTextChanged.invoke(it)
                    searchClear = it.isNotEmpty()
                },
                onSearch = {
                    active = false
                    searchMode = false
                    keyboardController?.hide()
                },
                active = active,
                onActiveChange = {
                    active = it
                },
                placeholder = { Text(hint) },
                leadingIcon = {
                    IconButton(onClick = {
                        active = false
                        searchMode = false
                        keyboardController?.hide()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                trailingIcon = {
                    if (searchClear) {
                        IconButton(onClick = {
                            searchClear = false
                            text = ""
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
            ) {
                content.invoke()
            }
        }
    }
}

@ExperimentalAnimationApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    title: String,
    searchHintText: String = "",
    menuItems: List<MenuItem> = listOf(),
    onClose: () -> Unit,
    onSearchTextChanged: (String) -> Unit = {},
) {

    var searchMode by remember { mutableStateOf(false) }
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchText by remember { mutableStateOf("") }
    val backgroundColor: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors()

    TopAppBar(
        title = {
            title3_leah(
                text = if (searchMode) "" else title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = backgroundColor,
        navigationIcon = {
                HsIconButton(onClick = {
                    if (searchMode) {
                        searchText = ""
                        onSearchTextChanged.invoke("")
                        searchMode = false
                    } else {
                        onClose.invoke()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = stringResource(R.string.Button_Back),
                    )
                }
            },
        actions = {
            if (searchMode) {
                val focusRequester = remember { FocusRequester() }
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 2.dp)
                        .focusRequester(focusRequester),
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        onSearchTextChanged.invoke(it)
                        showClearButton = it.isNotEmpty()
                    },
                    placeholder = {
                        body_grey50(text = searchHintText)
                    },
                    textStyle = ComposeAppTheme.typography.body,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = Color.Transparent,
                        cursorColor = ComposeAppTheme.colors.jacob,
                        textColor = ComposeAppTheme.colors.leah
                    ),
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    }),
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = showClearButton,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            HsIconButton(onClick = {
                                searchText = ""
                                onSearchTextChanged.invoke("")
                                showClearButton = false
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = stringResource(R.string.Button_Cancel),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                        }
                    },
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }

            if (!searchMode) {
                AppBarMenuButton(
                    icon = R.drawable.ic_search,
                    onClick = { searchMode = true },
                    description = stringResource(R.string.Button_Search),
                )

                menuItems.forEach { menuItem ->
                    if (menuItem.icon != null) {
                        AppBarMenuButton(
                            icon = menuItem.icon,
                            onClick = menuItem.onClick,
                            description = menuItem.title.getString(),
                            enabled = menuItem.enabled,
                            tint = menuItem.tint
                        )
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clickable(
                                    enabled = menuItem.enabled,
                                    onClick = menuItem.onClick
                                ),
                            text = menuItem.title.getString(),
                            style = ComposeAppTheme.typography.headline2,
                            color = if (menuItem.enabled) ComposeAppTheme.colors.jacob else ComposeAppTheme.colors.yellow50
                        )
                    }
                }
            }
        })

}
