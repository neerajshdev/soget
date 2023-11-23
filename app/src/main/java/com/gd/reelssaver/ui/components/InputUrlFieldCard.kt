package com.gd.reelssaver.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gd.reelssaver.ui.theme.AppTheme


@Preview
@Composable
private fun PageInputUrlPreview() {
    AppTheme {
        var url by remember { mutableStateOf("") }
        InputUrlFieldCard(
            url = url,
            onValueChange = { url = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}


@Composable
fun InputUrlFieldCard(
    url: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    onContentPaste: (String) -> Unit = {},
    onGoActionClick: () -> Unit = {},
    onKeyBoardAction: () -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current

    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Link,
                contentDescription = "Link input field",
                tint = colorScheme.outline
            )

            EditableUrlText(
                url = url,
                onValueChange = onValueChange,
                placeholderText = { Text(text = "Search or Type url") },
                onKeyBoardAction = onKeyBoardAction,
                modifier = Modifier.weight(1f)
            )

            ClipboardIconButton {
                val text = clipboardManager.getText()?.text?.trim()
                if (text != null) {
                    onContentPaste(text)
                }
            }

            AnimatedVisibility(visible = url.isNotBlank()) {
                GoActionIconButton(onGoActionClick)
            }
        }
    }
}


@Composable
fun ClipboardIconButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = colorScheme.tertiary,
            contentColor = colorScheme.onTertiary
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.ContentPaste,
            contentDescription = "Link input field",
        )
    }
}

@Composable
fun GoActionIconButton(onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = colorScheme.tertiary,
            contentColor = colorScheme.onTertiary
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowForward,
            contentDescription = "Link input field",
        )
    }
}

@Composable
fun EditableUrlText(
    url: String,
    textColor: Color = LocalContentColor.current,
    style: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier,
    placeholderText: @Composable () -> Unit = {},
    onValueChange: (String) -> Unit,
    onKeyBoardAction: () -> Unit = {}
) {

    var isEditing by remember { mutableStateOf(false) }

    BasicTextField(
        value = url,
        textStyle = style.copy(color = textColor),
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = { onKeyBoardAction() }),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                if (url.isBlank() && isEditing.not()) {
                    placeholderText()
                }
                innerTextField()
            }
        },
        modifier = modifier.onFocusChanged { focusState ->
            isEditing = focusState.hasFocus
        },
    )
}
