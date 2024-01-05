package com.gd.reelssaver.ui.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

interface RequiredPermission {
    fun check(): Boolean
    fun launch()
    fun shouldShowReason(): Boolean
    fun shouldOpenSetting(): Boolean
}

@Composable
fun requiredPermission(permissionString: String): RequiredPermission {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> }
    )

    return remember {
        object : RequiredPermission {
            private var tryCount = 0
            override fun check(): Boolean {
                return checkPermission(permissionString, context)
            }

            override fun launch() {
                launcher.launch(permissionString)
                tryCount++
            }

            override fun shouldShowReason(): Boolean {
                return tryCount > 0 && ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    permissionString
                )
            }

            override fun shouldOpenSetting(): Boolean =
                tryCount > 0 && !ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    permissionString
                )
        }
    }
}


private fun checkPermission(permissionString: String, context: Context): Boolean {
    if (permissionString == Manifest.permission.WRITE_EXTERNAL_STORAGE && Build.VERSION.SDK_INT > 28) return true

    return ActivityCompat.checkSelfPermission(
        context,
        permissionString
    ) == PackageManager.PERMISSION_GRANTED
}

