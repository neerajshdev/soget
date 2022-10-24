package com.njsh.instadl.util

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

@Composable
fun hasRWPerm(): Boolean {
    var returnValue: Boolean
    var perms: Array<String> = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val context = LocalContext.current
    returnValue = ActivityCompat.checkSelfPermission(
        context, perms[0]
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context, perms[1]
    ) == PackageManager.PERMISSION_GRANTED
    return returnValue
}


/**
 * Ask for read write permissions
 */
@Composable
fun AskForRWPerms(onAccept: () -> Unit, onReject: () -> Unit) {
    var perms: Array<String> = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var resultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { result ->
                if (result[perms[0]] == true && result[perms[1]] == true) {
                    onAccept()
                } else {
                    onReject()
                }
            })

    LaunchedEffect(key1 = Unit) {
        resultLauncher.launch(perms);
    }
}
