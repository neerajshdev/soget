package com.gd.reelssaver.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun AppAlertDialog(dialogState: AlertDialogState, modifier: Modifier = Modifier) {
    if (dialogState.dialogOpen) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                dialogState.dialogOpen = false
            },
            title = { Text(text = dialogState.title) },
            text = { Text(text = dialogState.text) },
            confirmButton = {
                TextButton(onClick = {
                    dialogState.dialogOpen = false
                    dialogState.confirmAction?.invoke()
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialogState.dialogOpen = false
                    dialogState.dismissAction?.invoke()
                }) {
                    Text("Dismiss")
                }
            }
        )
    }
}


class AlertDialogState {
    var dialogOpen by mutableStateOf(false)
    var title by mutableStateOf("")
    var text by mutableStateOf("")
    var confirmAction by mutableStateOf<(() -> Unit)?>(null)
    var dismissAction by mutableStateOf<(() -> Unit)?>(null)
    fun showDialog(
        title: String,
        text: String,
        confirmAction: (() -> Unit)? = null,
        dismissAction: (() -> Unit)? = null
    ) {
        dialogOpen = true
        this.title = title
        this.text = text
        this.confirmAction = confirmAction
        this.dismissAction = dismissAction
    }
}