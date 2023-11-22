package com.centicbhaiya.getitsocial.ui.state

import android.webkit.WebView
import online.desidev.onestate.OneState
import java.util.UUID

data class TabsScreenState(
    val tabs: MutableList<Tab> = mutableListOf(Tab()),
    var currentTabIndex: Int = 0
) {
    fun updateCurrentTab(tab: Tab): TabsScreenState {
        tabs[currentTabIndex] = tab
        return this
    }

    fun getCurrentTab() = tabs[currentTabIndex]

    fun addTab(tab: Tab) {
        tabs.add(tab)
        currentTabIndex = tabs.indexOf(tab)
    }

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

fun OneState<TabsScreenState>.goto(url: String) {
    send {
        it.apply {
            tabs[currentTabIndex] =
                tabs[currentTabIndex].copy(pageType = PageType.WEBPAGE, url = url).apply {
                    webView?.loadUrl(url)
                }
        }
    }
}

fun OneState<TabsScreenState>.updateTabUrl(url: String) {
    send {
        it.apply {
            tabs[currentTabIndex] = tabs[currentTabIndex].copy(url = url)
        }
    }
}

fun OneState<TabsScreenState>.closeWebPage() {
    send {
        it.apply {
            it.tabs[currentTabIndex] = it.tabs[currentTabIndex].copy(
                pageType = PageType.HOMEPAGE,
                url = null,
                webView = null
            )
        }
    }
}

fun OneState<TabsScreenState>.newTab(url: String? = null) {
    // add a new tab starts from homepage
    val tab = url?.let { Tab(url = it, pageType = PageType.WEBPAGE) } ?: Tab()
    send { it.apply { addTab(tab) } }
}

fun OneState<TabsScreenState>.removeTab(tab: Tab) {
    send {
        it.apply {
            if (tabs[currentTabIndex] == tab) {
                currentTabIndex = 0
            }
            tabs.remove(tab)
            if (tabs.isEmpty()) {
                addTab(Tab())
            }
        }
    }
}

fun OneState<TabsScreenState>.clearAll() {
    send {
        it.apply {
            tabs.clear()
            currentTabIndex = 0
            tabs.add(Tab())
        }
    }
}


