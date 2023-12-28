package com.gd.reelssaver.ui.screens.browser

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.gd.reelssaver.ui.router.ChildTabs
import com.gd.reelssaver.ui.router.TabNavigation
import com.gd.reelssaver.ui.router.addTab
import com.gd.reelssaver.ui.router.childTabs
import com.gd.reelssaver.ui.router.remove
import com.gd.reelssaver.ui.router.replaceAll
import com.gd.reelssaver.ui.router.select
import com.gd.reelssaver.ui.screens.browser.tab.DefaultTabComponent
import com.gd.reelssaver.ui.screens.browser.tab.TabComponent
import com.gd.reelssaver.ui.screens.browser.tab.TabComponentCallback
import com.gd.reelssaver.ui.util.componentScope
import com.gd.reelssaver.util.Events
import kotlinx.parcelize.Parcelize
import java.util.UUID


interface BrowserComponent : Events<Event> {
    val childTabs: Value<ChildTabs<Config, TabComponent>>
    val tabCount: Value<Int>
    val isTabChooserOpen: Value<Boolean>
    val tabComponents: Value<List<TabComponent>>
    val isDarkTheme: Value<Boolean>
}


sealed interface Event {
    data class AddNewTab(val initialPage: TabPage) : Event
    data class RemoveTab(val index: Int) : Event
    data class SelectTab(val index: Int) : Event
    data object ClearAllTab : Event
    data object OpenTabChooser : Event
    data object OnTabChooserDissmised : Event
}


@Parcelize
data class Config(
    val id: String = UUID.randomUUID().toString(),
    val initialPage: TabPage
) : Parcelable

sealed interface TabPage : Parcelable {

    @Parcelize
    data object Homepage : TabPage

    @Parcelize
    data class Webpage(val initialUrl: String) : TabPage
}

interface BrowserComponentCallback {
    fun toggleTheme()
    fun addDownload(url: String)
}


class DefaultBrowserComponent(
    context: ComponentContext,
    initialPage: TabPage,
    private val callback: BrowserComponentCallback,
    override val isDarkTheme: Value<Boolean>,
) : ComponentContext by context, BrowserComponent {
    private val _tabCount = MutableValue(0)
    override val tabCount = _tabCount

    private val _isTabChooserOpen = MutableValue(false)
    override val isTabChooserOpen: Value<Boolean> = _isTabChooserOpen

    private val scope = componentScope()

    private val navigation = TabNavigation<Config>(scope)

    override val childTabs: Value<ChildTabs<Config, TabComponent>> =
        childTabs(
            source = navigation,
            initialTabs = listOf(Config(initialPage = initialPage)),
            selectedTab = 0
        ) { config, componentContext ->
            DefaultTabComponent(
                componentContext,
                config.initialPage,
                tabCount,
                this.isDarkTheme,
                callback = object : TabComponentCallback {
                    override fun openTabChooser() {
                        _isTabChooserOpen.value = true
                    }

                    override fun toggleTheme() {
                        // event goes up
                        callback.toggleTheme()
                    }
                })
        }


    init {
        childTabs.observe {
            _tabCount.value = it.children.size
        }
    }

    override val tabComponents =
        childTabs.map { it.children.mapNotNull { child -> child.instance } }


    override fun onEvent(e: Event) {
        when (e) {
            is Event.AddNewTab -> navigation.addTab(Config(initialPage = e.initialPage))
            is Event.OpenTabChooser -> {}
            is Event.OnTabChooserDissmised -> {
                _isTabChooserOpen.value = false
            }

            is Event.RemoveTab -> {
                navigation.remove(e.index)
            }

            is Event.SelectTab -> {
                navigation.select(e.index)
            }

            is Event.ClearAllTab -> {
                navigation.replaceAll(homepageConfig())
            }
        }
    }

    private fun homepageConfig() = Config(initialPage = TabPage.Homepage)
}




