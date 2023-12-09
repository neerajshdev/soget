package com.gd.reelssaver.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.gd.reelssaver.ui.components.BrowserTopBar
import com.gd.reelssaver.ui.components.ComposeWebView
import com.gd.reelssaver.ui.navigation.FakeWebScreenComponent
import com.gd.reelssaver.ui.navigation.WebScreenComponent
import com.gd.reelssaver.ui.state.updateCurrentTab
import com.gd.reelssaver.ui.theme.AppTheme


@Preview
@Composable
private fun WebScreenContentPreview() {
    AppTheme {
        WebScreenContent(component = FakeWebScreenComponent())
    }
}

@Composable
fun WebScreenContent(component: WebScreenComponent) {
    val activeTab by component.activeTab.subscribeAsState()
    val tabs by component.tabs.subscribeAsState()
    val webView by remember {  }

    Scaffold(topBar = {
        BrowserTopBar(currentUrl = activeTab.url, tabCount = tabs.size, onOpenTabChooser = { })
    }) {
        ComposeWebView(
            modifier = Modifier.padding(it),
            initialUrl = activeTab.url,
            webView = null,
            onCreate = { webView ->
                tabsScreenState.updateCurrentTab(currentTab.copy(webView = webView))
            },
            onPageLoad = { newUrl ->
                component.onEvent(WebScreenComponent.Event.UpdateUrl(newUrl))
            }
        )
    }
}