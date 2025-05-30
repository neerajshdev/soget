package com.gd.reelssaver.ui.util

import android.util.Log
import android.view.Window
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext


fun ComponentContext.componentScope(context: CoroutineContext = Dispatchers.Main + SupervisorJob()): CoroutineScope {
    val scope = CoroutineScope(context)
    doOnDestroy { scope.cancel() }
    return scope
}

fun hideStatusBar(window: Window) {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.hide(WindowInsetsCompat.Type.statusBars())
    insetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}


fun showStatusBar(window: Window) {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.show(WindowInsetsCompat.Type.statusBars())
}

fun hideKeyBoard(window: Window) {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.hide(WindowInsetsCompat.Type.ime())
}

fun Color.lightness(lightness: Float): Color {
    val color = toArgb()
    val outHsl = FloatArray(3)
    ColorUtils.RGBToHSL(color.red, color.green, color.blue, outHsl)
    outHsl[2] = lightness
    return Color(ColorUtils.HSLToColor(outHsl))
}


@Composable
fun ComposeDebug(dbgStr: String) {
    Log.d("ComposeDebug", dbgStr)
}


fun Modifier.debugLine() = border(1.dp, color = Color.Green)