package com.gd.reelssaver.ui.screens.browser.tab

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.gd.reelssaver.ui.screens.browser.TabPage
import com.gd.reelssaver.ui.screens.browser.tab.pages.homepage.DefaultHomePage
import com.gd.reelssaver.ui.screens.browser.tab.pages.homepage.HomePage
import com.gd.reelssaver.ui.screens.browser.tab.pages.webpage.DefaultWebpage
import com.gd.reelssaver.ui.screens.browser.tab.pages.webpage.Webpage
import com.gd.reelssaver.util.Events
import kotlinx.parcelize.Parcelize
import java.util.UUID


interface TabComponent : Events<Event> {
    val child: Value<ChildStack<Config, ChildComp>>
}

class DefaultTabComponent(
    context: ComponentContext,
    initialPage: TabPage,
    private val tabsCount: Value<Int>,
    val isDarkTheme: Value<Boolean>
) : ComponentContext by context, TabComponent {

    private val navigation = StackNavigation<Config>()

    override val child: Value<ChildStack<Config, ChildComp>> = childStack(
        source = navigation,
        initialConfiguration = if (initialPage is TabPage.Webpage) Config.WebPage(initialUrl = initialPage.initialUrl) else Config.HomePage
    ) { config: Config, context: ComponentContext ->

        when (config) {
            is Config.HomePage -> ChildComp.HomepageComp(
                DefaultHomePage(
                    context, tabsCount, isDarkTheme
                )
            )

            is Config.WebPage -> ChildComp.WebpageComp(
                DefaultWebpage(context, isDarkTheme, tabsCount)
            )
        }
    }

    override fun onEvent(e: Event) {
        TODO("Not yet implemented")
    }
}

sealed interface Event {}

sealed interface Config : Parcelable {
    @Parcelize
    data object HomePage : Config

    @Parcelize
    data class WebPage(
        val id: String = UUID.randomUUID().toString(),
        val initialUrl: String
    ) : Config
}


sealed interface ChildComp {
    data class HomepageComp(val homePage: HomePage) : ChildComp
    data class WebpageComp(val webpage: Webpage) : ChildComp
}