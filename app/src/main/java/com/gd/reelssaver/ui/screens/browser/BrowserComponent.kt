package com.gd.reelssaver.ui.screens.browser

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.gd.reelssaver.ui.blocs.componentScope
import com.gd.reelssaver.ui.router.ChildTabs
import com.gd.reelssaver.ui.router.TabNavigation
import com.gd.reelssaver.ui.router.childTabs
import com.gd.reelssaver.ui.screens.browser.tab.DefaultTabComponent
import com.gd.reelssaver.ui.screens.browser.tab.TabComponent
import com.gd.reelssaver.util.Events
import kotlinx.parcelize.Parcelize
import java.util.UUID


interface BrowserComponent : Events<Event> {
    val tabs: Value<ChildTabs<Config, TabComponent>>
    val tabsCount: Value<Int>
}


class DefaultBrowserComponent(
    context: ComponentContext,
    val isDarkTheme: Value<Boolean>
) : ComponentContext by context, BrowserComponent {


    private val scope = componentScope()

    private val navigation = TabNavigation<Config>(scope)

    override val tabs: Value<ChildTabs<Config, TabComponent>> =
        childTabs(
            source = navigation,
            initialTabs = listOf(Config(initialPage = TabPage.Homepage)),
            selectedTab = 0
        ) { config, componentContext ->
            DefaultTabComponent(componentContext, config.initialPage, tabsCount, isDarkTheme)
        }

    override val tabsCount = tabs.map { it.inActive.size + 1 }
    override fun onEvent(e: Event) {
    }
}

sealed interface Event {
    data class AddNewTab(val initialPage: TabPage) : Event
}


sealed interface TabPage : Parcelable {

    @Parcelize
    data object Homepage : TabPage

    @Parcelize
    data class Webpage(val initialUrl: String) : TabPage
}

@Parcelize
data class Config(
    val id: String = UUID.randomUUID().toString(),
    val initialPage: TabPage
) : Parcelable

