package com.gd.reelssaver.ui.blocs

import android.webkit.WebView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.parcelable.Parcelable
import com.gd.reelssaver.ui.router.TabNavState
import com.gd.reelssaver.ui.router.TabNavigation
import com.gd.reelssaver.ui.router.childTabs
import com.gd.reelssaver.ui.router.goBackward
import com.gd.reelssaver.ui.router.goForward
import com.gd.reelssaver.ui.router.goto
import com.gd.reelssaver.ui.router.replaceCurrent
import com.gd.reelssaver.util.findFirstUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.net.URL
import java.util.UUID
import kotlin.coroutines.CoroutineContext


typealias WebPageModels = Map<String, RootComponent.WebPageModel>

fun ComponentContext.componentScope(context: CoroutineContext = Dispatchers.Main + SupervisorJob()): CoroutineScope {
    val scope = CoroutineScope(context)
    doOnDestroy { scope.cancel() }
    return scope
}

class RootComponent(componentContext: ComponentContext, val onAppClose: () -> Unit) :
    ComponentContext by componentContext {
    data class WebPageModel(
        val id: String,
        val currentUrl: MutableStateFlow<String>,
        val view: MutableStateFlow<WebView?>,
    )

    private val webPages = MutableStateFlow<WebPageModels>(mapOf())
    private val pageCount by lazy {
        val mutableStateFlow = MutableStateFlow(0)
        scope.launch {
            webPages.collect {
                mutableStateFlow.value = it.size
            }
        }
        mutableStateFlow
    }

    private val scope = componentScope()

    val extraUrl: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _useDarkTheme = MutableStateFlow(false)
    val useDarkTheme: StateFlow<Boolean> = _useDarkTheme

    private val sheetNav = SlotNavigation<SheetConfig>()
    private val navigation = TabNavigation<Config>(scope)
    val child = childTabs(
        source = navigation,
        configClass = Config::class,
        initialState = {
            TabNavState(
                mapOf(Config.SplashScreen to emptySet()),
                Config.SplashScreen
            )
        },
        childFactory = ::childFactory
    )


    private fun clearAllWebPages() {

    }

    private fun childFactory(config: Config, ctx: ComponentContext): Child {
        return when (config) {
            is Config.SplashScreen -> Child.SplashScreen(
                DefaultSplashScreenComponent(ctx, onSplashComplete = {
                    navigation.replaceCurrent(Config.HomeScreen) {
                        if (extraUrl.value != null) {
                            val url = findFirstUrl(extraUrl.value!!)
                            if (url != null) {
                                navigation.goForward(
                                    Config.WebScreen(
                                        initialUrl = url,
                                        pageModelId = UUID.randomUUID().toString()
                                    )
                                )
                            }
                        }
                    }
                })
            )

            is Config.HomeScreen -> Child.HomeScreenChild(
                DefaultHomeScreenComponent(ctx,
                    pageCount = pageCount,
                    isDarkTheme = useDarkTheme,
                    onOpenWebUrl = { webUrl: URL ->
                        openNewWebPage(webUrl.toString())
                    },
                    onOpenTabChooser = {
                        sheetNav.activate(SheetConfig.TabChooser)
                    },
                    onToggleTheme = {
                        _useDarkTheme.value = useDarkTheme.value.not()
                    }
                )
            )

            is Config.WebScreen -> {
                val model = WebPageModel(
                    currentUrl = MutableStateFlow(config.initialUrl),
                    view = MutableStateFlow(null),
                    id = config.pageModelId
                )

                webPages.value = webPages.value.toMutableMap().apply {
                    put(config.pageModelId, model)
                }

                Child.WebScreenChild(
                    DefaultWebScreenComponent(
                        componentContext = ctx,
                        webView = model.view,
                        currentUrl = model.currentUrl,
                        isDarkTheme = useDarkTheme,
                        pageCount = pageCount,
                        onOpenTabChooser = {
                            sheetNav.activate(SheetConfig.TabChooser)
                        },
                        onGoBackToHome = {
                            navigation.goBackward()
                        },
                        onToggleTheme = {
                            _useDarkTheme.value = useDarkTheme.value.not()
                        },
                        onViewCreated = { model.view.value = it },
                        onPageLoaded = { pageUrl -> model.currentUrl.value = pageUrl },
                        onComponentDestroyed = {
                            val newState = webPages.value.toMutableMap().apply {
                                remove(config.pageModelId)
                            }
                            webPages.value = newState
                        }
                    )
                )
            }
        }
    }


    private fun openNewWebPage(url: String) {
        // goto home screen and then go forward to new web screen
        navigation.goto(Config.HomeScreen) {
            navigation.goForward(
                Config.WebScreen(
                    initialUrl = url,
                    pageModelId = UUID.randomUUID().toString()
                )
            )
        }
    }


    private fun removeWebPage(pageId: String) {
        val config = child.value.graph.keys.find {config ->
            config is Config.WebScreen && config.pageModelId == pageId
        }

        val selectedConfig = child.value.active.configuration

        if (config != null && selectedConfig != config) {
            with(navigation) {
                goto(config) {}
                goBackward()
                goForward(selectedConfig)
            }
        } else {
            navigation.goBackward()
        }
    }

    private fun selectPage(pageId: String) {
        val config = child.value.graph.keys.find {config ->
            config is Config.WebScreen && config.pageModelId == pageId
        }

        if (config != null) {
            navigation.goto(config)
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OpenNewTabWithExtraText -> {
                openNewWebPage(event.extraText)
            }

            Event.DismissBottomSheet -> sheetNav.dismiss()
            Event.ShowExitPrompt -> sheetNav.activate(SheetConfig.ExitPrompt)
        }
    }

    sealed class Event {
        data class OpenNewTabWithExtraText(val extraText: String) : Event()
        data object DismissBottomSheet : Event()
        data object ShowExitPrompt : Event()
    }


    private val activePage by lazy {
        val state = MutableStateFlow<WebPageModel?>(null)
        child.subscribe { newValue ->
            val currentConfig = newValue.active.configuration
            if (currentConfig is Config.WebScreen) {
                state.value = webPages.value[currentConfig.pageModelId]
            }
        }
        state
    }

    val bottomSheet = childSlot(
        source = sheetNav,
        handleBackButton = true
    ) { configuration: SheetConfig, componentContext: ComponentContext ->
        when (configuration) {
            SheetConfig.TabChooser -> DefaultTabChooserComponent(
                componentContext = componentContext,
                selectedPage = activePage,
                pages = webPages.map { it.values.toList() },
                onAddNewTab = {
                    val googleSearch = "www.google.com"
                    openNewWebPage(googleSearch)
                },
                onClearAllPages = { clearAllWebPages() },
                onRemoveTab = { removeWebPage(it) },
                onSelectTab = { selectPage(it) },
                onBackClick = { navigation.goBackward() }
            ) {
            }

            SheetConfig.ExitPrompt -> DefaultExitPromptComponent(
                componentContext,
                onExitCancel = {
                    sheetNav.dismiss()
                },
                onExitConfirm = {
                    onAppClose()
                    sheetNav.dismiss()
                }
            )
        }
    }

    sealed class Config : Parcelable {
        @Parcelize
        data object SplashScreen : Config()

        @Parcelize
        data object HomeScreen : Config()

        @Parcelize
        data class WebScreen(val pageModelId: String, val initialUrl: String) : Config()
    }

    sealed class Child {
        data class SplashScreen(val component: SplashScreenComponent) : Child()
        data class HomeScreenChild(val component: HomeScreenComponent) : Child()
        data class WebScreenChild(val component: WebScreenComponent) : Child()
    }


    sealed class SheetConfig : Parcelable {
        @Parcelize
        data object TabChooser : SheetConfig()

        @Parcelize
        data object ExitPrompt : SheetConfig()
    }
}