package com.gd.reelssaver.ui.composables

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gd.reelssaver.R
import com.gd.reelssaver.ui.screens.PageCountIcon
import com.gd.reelssaver.ui.theme.AppTheme

@Composable
private fun rememberTextStyle() = typography.bodyLarge

@Preview
@Composable
fun BrowserTopBarPrev() {
    AppTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            BrowserTopBar(
                currentUrl = "https://example.com",
                onOpenTabChooser = {},
                tabCount = 2
            )
        }
    }
}

enum class BrowserTopBarContent {
    EditableUrl, CurrentPage
}

@Composable
fun BrowserTopBar(
    modifier: Modifier = Modifier,
    currentUrl: String,
    tabCount: Int,
    useDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onOpenTabChooser: () -> Unit= {},
    onLoadNewPage: (String) -> Unit = {},
) {
    var contentType by remember {
        mutableStateOf(BrowserTopBarContent.CurrentPage)
    }
    AnimatedContent(
        modifier = modifier,
        targetState = contentType,
        label = "AnimatedContent",
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }) { type ->
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50.dp),
            color = colorScheme.surfaceContainerLowest
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .requiredHeight(48.dp)
            ) {
                when (type) {
                    BrowserTopBarContent.CurrentPage -> {
                        CurrentPageUrlText(
                            currentUrl = currentUrl,
                            tabCount = tabCount,
                            onOpenTabChooser = onOpenTabChooser,
                            useDarkTheme = useDarkTheme,
                            onToggleTheme = onToggleTheme,
                            modifier = Modifier
                                .clickable(
                                    enabled = contentType == BrowserTopBarContent.CurrentPage,
                                    onClick = { contentType = BrowserTopBarContent.EditableUrl },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .fillMaxWidth()
                        )
                    }

                    BrowserTopBarContent.EditableUrl -> {
                        EditableUrlText(
                            initialValue = currentUrl,
                            onLoadNewPage = {
                                onLoadNewPage(it)
                                contentType = BrowserTopBarContent.CurrentPage
                            },
                            onCancelEditTextField = {
                                contentType = BrowserTopBarContent.CurrentPage
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun CurrentPageUrlText(
    modifier: Modifier = Modifier,
    currentUrl: String,
    tabCount: Int,
    useDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenTabChooser: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Link,
            contentDescription = "Link input field",
            tint = colorScheme.outline,
        )
        Text(
            text = currentUrl,
            style = rememberTextStyle(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onOpenTabChooser) {
            PageCountIcon(count = tabCount)
        }

        IconButton(onClick = onToggleTheme) {
            Icon(
                painter = painterResource(id = if (useDarkTheme) R.drawable.baseline_dark_mode_24 else R.drawable.baseline_light_mode_24),
                contentDescription = "Change Theme",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditableUrlText(
    initialValue: String,
    onLoadNewPage: (String) -> Unit,
    onCancelEditTextField: () -> Unit
) {
    var editableText by remember { mutableStateOf(initialValue) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardVisible = WindowInsets.isImeVisible
    var hadVisible by remember { mutableStateOf(false) }

    SideEffect {
        focusRequester.requestFocus()
    }

    LaunchedEffect(keyboardVisible) {
        Log.d("EditableUrlText", "keyboardVisible: $keyboardVisible, hadFocus: $hadVisible")
        if (!keyboardVisible && hadVisible) {
            focusManager.clearFocus()
            onCancelEditTextField()
        }
        hadVisible = keyboardVisible
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(
            imageVector = Icons.Rounded.Link,
            contentDescription = "Link input field",
            tint = colorScheme.outline
        )

        BasicTextField(
            value = editableText,
            onValueChange = { editableText = it },
            textStyle = rememberTextStyle(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = {
                if (editableText.isNotBlank()) onLoadNewPage(
                    editableText.trim()
                )
            }),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
        )

        IconButton(onClick = { editableText = "" }) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
        }
    }
}