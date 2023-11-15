package com.centicbhaiya.getitsocial.ui.state

import android.webkit.WebView

data class TabsScreenState(
    private val tabs: MutableList<Tab> = mutableListOf(Tab()),
    private val currentTabIndex: Int = 0
) {
    fun updateCurrentTab(tab: Tab): TabsScreenState {
        tabs[currentTabIndex] = tab
        return this
    }

    fun getTabCount() = tabs.size

    fun getTabs(): List<Tab> = tabs

    fun getCurrentTab() = tabs[currentTabIndex]
}

data class Tab(
    val pageType: PageType = PageType.HOMEPAGE,
    val url: String? = null,
    val webView: WebView? = null,
)

enum class PageType {
    WEBPAGE, HOMEPAGE
}