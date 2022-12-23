package com.njsh.reelssaver.layer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.njsh.reelssaver.App
import com.njsh.reelssaver.layer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputUrlTaker(
    modifier: Modifier = Modifier,
    verifyInput: (String) -> Boolean,
    onInput: (String) -> Unit,
) {
    var inputString by remember { mutableStateOf("") }
    var isGetButtonEnabled by remember { mutableStateOf(false) }

    val onInputStringChange: (String) -> Unit = {
        inputString = it
        isGetButtonEnabled = verifyInput(inputString)
    }

    val onPasteButtonClick: () -> Unit = {
        inputString = App.clipBoardData()
        isGetButtonEnabled = verifyInput(inputString)
    }

    Column(modifier = modifier) {
        TextField(
            value = inputString,
            onValueChange = onInputStringChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row {
            OutlinedButton(onClick = onPasteButtonClick, modifier = Modifier.weight(1f)) {
                Text(text = "PASTE")
            }

            Spacer(modifier = Modifier.width(16.dp))

            FilledTonalButton(
                onClick = { onInput(inputString) },
                enabled = isGetButtonEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "GET")
            }
        }
    }
}


@Preview
@Composable
fun InputUrlTakerP() {
    AppTheme {
        Surface {
            InputUrlTaker(verifyInput = { true }, onInput = {})
        }
    }
}