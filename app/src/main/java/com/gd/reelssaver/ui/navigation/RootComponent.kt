package com.gd.reelssaver.ui.navigation

import android.webkit.WebView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.parcelable.Parcelable
import com.gd.reelssaver.model.Tab
import com.gd.reelssaver.util.findFirstUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize

class RootComponent(componentContext: ComponentContext) : ComponentContext by componentContext {
    private val tabs = MutableValue(emptyList<Tab>())
    private val activeTab = MutableStateFlow<Tab?>(null)
    private val views: HashMap<String, WebView> = HashMap()

    val extraUrl: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _useDarkTheme = MutableStateFlow(false)
    val useDarkTheme: StateFlow<Boolean> = _useDarkTheme

    private val navigation = StackNavigation<Config>()
    val child = childStack<Config, Child>(
        source = navigation,
        initialConfiguration = Config.SplashScreen,
        handleBackButton = false,
        childFactory = ::childFactory,
    )

    private val sheetNav = SlotNavigation<SheetConfig>()

    private fun clearAllTab() {
        tabs.value = emptyList()
        views.clear()
        activeTab.value = null
    }

    @OptIn(ExperimentalDecomposeApi::class)
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
                onAddNewTab = {
                    val newTab = Tab(url = "www.google.com")
                    tabs.value += newTab
                    activeTab.value = newTab

                    if (child.value.active.configuration == Config.HomeScreen) {
                        navigation.pushNew(Config.WebScreen)
                    }
                },
                onClearAllTab = {
                    if (child.value.active.configuration is Config.WebScreen) {
                        navigation.pop { clearAllTab() }
                    } else {
                        clearAllTab()
                    }
                },
                onRemoveTab = {
                    tabs.value -= it
                    views.remove(it.id)
                    if (it == activeTab.value) {
                        activeTab.value = null
                        if (child.value.active.configuration is Config.WebScreen) {
                            navigation.pop()
                        }
                    }
                },
                onSelectTab = {
                    activeTab.value = it
                    if (child.active.configuration is Config.HomeScreen) {
                        navigation.pushNew(Config.WebScreen)
                    }
                },
                onBackClick = {
                    views[activeTab.value?.id]?.run {
                        if (canGoBack()) {
                            goBack()
                        }
                    }
                },
                onForwardClick = {
                    views[activeTab.value?.id]?.run {
                        if (canGoForward()) {
                            goForward()
                        }
                    }
                }
            )
        }
    }


    @OptIn(ExperimentalDecomposeApi::class)
    private fun childFactory(config: Config, ctx: ComponentContext): Child {
        return when (config) {
            is Config.SplashScreen -> Child.SplashScreen(
                DefaultSplashScreenComponent(ctx, onSplashComplete = {
                    navigation.replaceCurrent(Config.HomeScreen) {
                        if (extraUrl.value != null) {
                            val url = findFirstUrl(extraUrl.value!!)
                            if (url != null) {
                                navigation.pushNew(Config.WebScreen)
                            }
                        }
                    }
                })
            )
            is Config.HomeScreen -> Child.HomeScreenChild(
                DefaultHomeScreenComponent(ctx,
                    tabs = tabs,
                    useDarkTheme = useDarkTheme,
                    onOpenWebUrl = { webUrl ->
                        tabs.value += Tab(webUrl.toString()).also { activeTab.value = it }
                        navigation.pushNew(Config.WebScreen)
                    },
                    onOpenTabChooser = {
                        sheetNav.activate(SheetConfig.TabChooser)
                    },
                    onToggleTheme = {
                        _useDarkTheme.value = useDarkTheme.value.not()
                    }
                )
            )

            is Config.WebScreen -> Child.WebScreenChild(
                DefaultWebScreenComponent(
                    componentContext = ctx,
                    tabs = tabs,
                    activeTab = activeTab,
                    views = views,
                    useDarkTheme = useDarkTheme,
                    onTabUpdate = { old, new ->
                        tabs.value = tabs.value.map { if (it == old) new else it }
                        activeTab.value = new
                    },
                    onWebViewCreated = { key, view ->
                        views[key] = view
                    },
                    onOpenTabChooser = {
                        sheetNav.activate(SheetConfig.TabChooser)
                    },
                    onGoBackToHome = {
                        navigation.pop {
                            activeTab.value?.let {
                                tabs.value -= it
                            }
                            activeTab.value = null
                        }
                    },
                    onToggleTheme = {
                        _useDarkTheme.value = useDarkTheme.value.not()
                    },
                    onLoadUrl = { url ->
                        activeTab.value?.let {
                            views[it.id]?.loadUrl(url.toString())
                        }
                    }
                )
            )
        }
    }


    @OptIn(ExperimentalDecomposeApi::class)
    fun onEvent(event: Event) {
        when(event) {
            is Event.OpenNewTabWithExtraText -> {
                tabs.value += Tab(event.extraText).also { activeTab.value = it }
                if (child.value.active.configuration is Config.HomeScreen) {
                    navigation.pushNew(Config.WebScreen)
                }
            }
        }
    }

    sealed class Event {
        data class OpenNewTabWithExtraText(val extraText: String): Event()
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