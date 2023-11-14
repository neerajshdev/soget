package com.centicbhaiya.getitsocial.ui.util

import android.view.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


fun hideStatusBar(window: Window) {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.hide(WindowInsetsCompat.Type.statusBars())
    insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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