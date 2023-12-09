package com.gd.reelssaver.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.parcelable.Parcelable
import com.gd.reelssaver.ui.model.Tab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.parcelize.Parcelize
import kotlin.jvm.*

internal class Symbol(@JvmField val symbol: String) {
    override fun toString(): String = "<$symbol>"

    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
    inline fun <T> unbox(value: Any?): T = if (value === this) null as T else value as T
}

class RootComponent(componentContext: ComponentContext) : ComponentContext by componentContext {
    private val tabs = MutableValue(emptyList<Tab>())
    private val activeTab = MutableValue(Tab("Unknown"))
    private val flow = MutableStateFlow<String?>(null)

    private val navigation = StackNavigation<Config>()
    val child = childStack<Config, Child>(
        source = navigation,
        initialConfiguration = Config.HomeScreen,
        handleBackButton = true,
        childFactory = ::childFactory,
    )


    private fun childFactory(config: Config, ctx: ComponentContext): Child {
        return when (config) {
            is Config.HomeScreen -> Child.HomeScreenChild(
                DefaultHomeScreenComponent(ctx, tabs = tabs, onOpenWebUrl = { webUrl ->
                    tabs.value += Tab(webUrl.toString()).also { activeTab.value = it }
                    navigation.push(Config.WebScreen)
                })
            )

            is Config.WebScreen -> Child.WebScreenChild(
                DefaultWebScreenComponent(
                    componentContext = ctx,
                    tabs = tabs,
                    activeTab = activeTab,
                    onTabUpdate = { old, new ->
                        tabs.value = tabs.value.map { if (it == old) new else it }
                        activeTab.value = new
                    }
                )
            )

            is Config.SplashScreen -> Child.SplashScreen(
                DefaultSplashScreenComponent(ctx, onSplashComplete = {
                    navigation.replaceCurrent(Config.HomeScreen)
                })
            )
        }
    }

    sealed class Config : Parcelable {
        @Parcelize
        data object SplashScreen : Config()

        @Parcelize
        data object HomeScreen : Config()

        @Parcelize
        data object WebScreen : Config()
    }

    sealed class Child {
        data class SplashScreen(val component: SplashScreenComponent) : Child()
        data class HomeScreenChild(val component: HomeScreenComponent) : Child()
        data class WebScreenChild(val component: WebScreenComponent) : Child()
    }
}