package com.gd.reelssaver.ui.navigation

import android.webkit.WebView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.parcelable.Parcelable
import com.gd.reelssaver.ui.model.Tab
import kotlinx.parcelize.Parcelize

internal class Symbol(@JvmField val symbol: String) {
    override fun toString(): String = "<$symbol>"

    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
    inline fun <T> unbox(value: Any?): T = if (value === this) null as T else value as T
}

class RootComponent(componentContext: ComponentContext) : ComponentContext by componentContext {
    private val tabs = MutableValue(emptyList<Tab>())
    private val activeTab = MutableValue(Tab("Unknown"))
    private val views: HashMap<String, WebView> = HashMap()

    private val navigation = StackNavigation<Config>()
    val child = childStack<Config, Child>(
        source = navigation,
        initialConfiguration = Config.SplashScreen,
        handleBackButton = true,
        childFactory = ::childFactory,
    )

    private val sheetNav = SlotNavigation<SheetConfig>()

    val bottomSheet = childSlot(
        source = sheetNav,
        handleBackButton = true
    ) { configuration: SheetConfig, componentContext: ComponentContext ->
        when (configuration) {
            SheetConfig.TabChooser -> DefaultBottomSheetComponent(
                componentContext = componentContext,
                activeTab = activeTab,
                tabs = tabs,
                views = views,
                onBottomSheetClose = { sheetNav.dismiss() },
                // Todo: Handle all the Callbacks
                onAddNewTab = { },
                onClearAllTab = { },
                onRemoveTab = { },
                onSelectTab = { },
                onBackClick = { },
                onForwardClick = { }
            )
        }
    }


    private fun childFactory(config: Config, ctx: ComponentContext): Child {
        return when (config) {
            is Config.HomeScreen -> Child.HomeScreenChild(
                DefaultHomeScreenComponent(ctx,
                    tabs = tabs,
                    onOpenWebUrl = { webUrl ->
                        tabs.value += Tab(webUrl.toString()).also { activeTab.value = it }
                        navigation.push(Config.WebScreen)
                    },
                    onOpenTabChooser = {
                        sheetNav.activate(SheetConfig.TabChooser)
                    }
                )
            )

            is Config.WebScreen -> Child.WebScreenChild(
                DefaultWebScreenComponent(
                    componentContext = ctx,
                    tabs = tabs,
                    activeTab = activeTab,
                    views = views,
                    onTabUpdate = { old, new ->
                        tabs.value = tabs.value.map { if (it == old) new else it }
                        activeTab.value = new
                    },
                    onWebViewCreated = { key, view ->
                        views[key] = view
                    },
                    onOpenTabChooser = {
                        sheetNav.activate(SheetConfig.TabChooser)
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


    sealed class SheetConfig : Parcelable {
        @Parcelize
        data object TabChooser : SheetConfig()
    }
}