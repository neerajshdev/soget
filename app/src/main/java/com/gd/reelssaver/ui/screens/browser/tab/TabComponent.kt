package com.gd.reelssaver.ui.screens.browser.tab

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.gd.reelssaver.ui.screens.browser.TabPage
import com.gd.reelssaver.ui.screens.browser.tab.pages.homepage.DefaultHomePageComponent
import com.gd.reelssaver.ui.screens.browser.tab.pages.homepage.HomePageComponent
import com.gd.reelssaver.ui.screens.browser.tab.pages.homepage.HomepageComponentCallback
import com.gd.reelssaver.ui.screens.browser.tab.pages.webpage.DefaultWebpageComponent
import com.gd.reelssaver.ui.screens.browser.tab.pages.webpage.WebpageComponent
import com.gd.reelssaver.ui.screens.browser.tab.pages.webpage.WebpageComponentCallback
import kotlinx.parcelize.Parcelize
import java.net.URL
import java.util.UUID


interface TabComponent {
    val child: Value<ChildStack<Config, ChildComp>>
}

class DefaultTabComponent(
    context: ComponentContext,
    initialPage: TabPage,

    private val tabCount: Value<Int>,
    val isDarkTheme: Value<Boolean>,
    val callback: TabComponentCallback,
) : ComponentContext by context, TabComponent {

    private val navigation = StackNavigation<Config>()

    override val child: Value<ChildStack<Config, ChildComp>> = childStack(
        source = navigation,
        initialConfiguration = if (initialPage is TabPage.Webpage) Config.WebPage(initialUrl = initialPage.initialUrl) else Config.HomePage,
        handleBackButton = true
    ) { config: Config, context: ComponentContext ->

        when (config) {
            is Config.HomePage -> ChildComp.Homepage(
                DefaultHomePageComponent(
                    context, tabCount, isDarkTheme, callback = object : HomepageComponentCallback {
                        override fun onOpenWebSite(url: URL) {
                            navigation.push(Config.WebPage(initialUrl = url.toString()))
                        }

                        override fun openTabChooser() {
                            callback.openTabChooser()
                        }

                        override fun toggleTheme() {
                            callback.toggleTheme()
                        }
                    }
                )
            )

            is Config.WebPage -> ChildComp.Webpage(
                DefaultWebpageComponent(
                    context,
                    config.initialUrl,
                    isDarkTheme,
                    tabCount,
                    callback = object : WebpageComponentCallback {
                        override fun openTabChooser() {
                            callback.openTabChooser()
                        }

                        override fun toggleTheme() {
                            callback.toggleTheme()
                        }
                    }
                )
            )
        }
    }
}

interface TabComponentCallback {
    fun openTabChooser()
    fun toggleTheme()
}

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
    data class Homepage(val homePage: HomePageComponent) : ChildComp
    data class Webpage(val webpage: WebpageComponent) : ChildComp
}