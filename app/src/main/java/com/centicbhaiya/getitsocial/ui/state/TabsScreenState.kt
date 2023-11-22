package com.centicbhaiya.getitsocial.ui.state

import android.webkit.WebView
import online.desidev.onestate.OneState
import java.util.UUID

data class TabsScreenState(
    val tabs: List<Tab> = listOf(Tab()),
    var currentTabIndex: Int = 0
) {
    fun getCurrentTab() = tabs[currentTabIndex]
}

data class Tab(
    val id: String = UUID.randomUUID().toString(),
    val pageType: PageType = PageType.HOMEPAGE,
    val url: String? = null,
    val webView: WebView? = null,
)

enum class PageType {
    WEBPAGE, HOMEPAGE
}


fun OneState<TabsScreenState>.selectTab(tab: Tab) {
    send {
        it.apply {
            currentTabIndex = tabs.indexOf(tab)
        }
    }
}

fun OneState<TabsScreenState>.updateCurrentTab(
    newTab: Tab
) {
    send {
        val currTab = it.getCurrentTab()
        val tabs = it.tabs.map { tab: Tab ->
            if (tab == currTab) {
                newTab
            } else tab
        }
        val index = it.currentTabIndex
        TabsScreenState(tabs, index)
    }
}

fun OneState<TabsScreenState>.goto(url: String) {
    send {
        val tabs = it.tabs.toMutableList()
        val updatedTab = it.getCurrentTab().copy(pageType = PageType.WEBPAGE, url = url).apply {
            webView?.loadUrl(url)
        }
        tabs[it.currentTabIndex] = updatedTab
        it.copy(tabs = tabs)
    }
}

fun OneState<TabsScreenState>.updateTabUrl(url: String) {
    send {
        val tabs = it.tabs.toMutableList()
        tabs[it.currentTabIndex] = it.getCurrentTab().copy(url = url)
        it.copy(tabs = tabs)
    }
}

fun OneState<TabsScreenState>.closeWebPage() {
    send {
        val tabs = it.tabs.toMutableList()
        tabs[it.currentTabIndex] = Tab()
        it.copy(tabs = tabs)
    }
}

fun OneState<TabsScreenState>.newTab(url: String? = null) {
    // add a new tab starts from homepage
    val tab = url?.let { Tab(url = it, pageType = PageType.WEBPAGE) } ?: Tab()
    send {
        val tabs = it.tabs.toMutableList()
        tabs.add(tab)
        TabsScreenState(tabs, tabs.lastIndex)
    }
}

fun OneState<TabsScreenState>.removeTab(tab: Tab) {
    send { state ->
        val tabs = state.tabs.toMutableList()
        val currTab =  state.getCurrentTab()
        tabs.remove(tab)
        var tabIndex = tabs.indexOf(currTab).coerceAtLeast(0)

        if (tabs.isEmpty()) {
            tabs.add(Tab())
            tabIndex = 0
        }

        TabsScreenState(tabs, tabIndex)
    }
}

fun OneState<TabsScreenState>.clearAll() {
    send { TabsScreenState() }
}


