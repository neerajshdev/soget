package com.gd.reelssaver.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gd.reelssaver.ui.theme.AppTheme


@Preview
@Composable
private fun ExitDialogPreview() {
    AppTheme {
        ExitDialog(
            onExitCancel = {},
            onExitConfirm = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExitDialogBottomSheet(
    enable: Boolean,
    onDismiss: () -> Unit,
    onExitConfirm: () -> Unit = {},
    onExitCancel: () -> Unit = {}
) {
    if (enable) {
        val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = state
        ) {
            ExitDialog(
                onExitConfirm = onExitConfirm,
                onExitCancel = onExitCancel
            )
        }

        LaunchedEffect(Unit) {
            state.expand()
        }
    }
}

@Composable
fun ExitDialog(
    onExitConfirm: () -> Unit = {},
    onExitCancel: () -> Unit = {}
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
        ) {
            Text(
                text = "Do you really want to exit?",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            MediumSizeNativeAd {}
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                TextButton(onClick = onExitCancel) {
                    Text(text = "No")
                }

                Button(onClick = onExitConfirm) {
                    Text(text = "Yes")
                }
            }
        }
    }
}