package com.centicbhaiya.getitsocial.ui.state

data class TabsScreenState(
    val tabs: List<Tab>,
    val currentTabIndex: Int = 0
) {
    fun updateUrl(url: String): TabsScreenState {
        val tabs = tabs.toMutableList()
        tabs[currentTabIndex] = Tab(url)
        return TabsScreenState(tabs, currentTabIndex)
    }

    fun getCurrentTabUrl() = tabs[currentTabIndex]
}

data class Tab(
    val url: String,
)