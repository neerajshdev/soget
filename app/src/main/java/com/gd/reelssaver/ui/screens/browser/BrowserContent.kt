package com.gd.reelssaver.ui.screens.browser

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun BrowserContent(c: BrowserComponent) {
    val tabs = c.tabs.subscribeAsState()
}