package com.gd.reelssaver.ui.screens

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.desidev.downloader.Downloader
import com.gd.reelssaver.componentmodels.DefaultDownloadModel
import com.gd.reelssaver.componentmodels.DownloadModel
import com.gd.reelssaver.ui.screens.browser.BrowserComponent
import com.gd.reelssaver.ui.screens.browser.BrowserComponentCallback
import com.gd.reelssaver.ui.screens.browser.DefaultBrowserComponent
import com.gd.reelssaver.ui.screens.browser.TabPage
import com.gd.reelssaver.ui.screens.downloads.DefaultDownloadsComponent
import com.gd.reelssaver.ui.screens.downloads.DownloadComponentCallback
import com.gd.reelssaver.ui.screens.downloads.DownloadsComponent
import com.gd.reelssaver.ui.screens.splash.DefaultSplashComponent
import com.gd.reelssaver.ui.screens.splash.SplashComponent
import com.gd.reelssaver.ui.screens.splash.SplashComponentCallback
import com.gd.reelssaver.util.Events
import kotlinx.parcelize.Parcelize
import java.io.File

interface RootComponent : Events<Event> {
    val childStack: Value<ChildStack<Config, Child>>
    val isDarkTheme: Value<Boolean>
}

sealed interface Event {
    object OnTabMenuSelect : Event
    object OnDownloadMenuSelect : Event
}

sealed interface Config : Parcelable {
    @Parcelize
    data object Splash : Config

    @Parcelize
    data class Browser(val initialPage: TabPage) : Config

    @Parcelize
    data object Downloads : Config
}

sealed interface Child {
    data class Browser(val component: BrowserComponent) : Child
    data class Downloads(val component: DownloadsComponent) : Child
    data class Splash(val component: SplashComponent) : Child
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    downloader: Downloader,
    parentVideoDir: File,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<Config, Child>> =
        childStack(
            source = navigation,
            handleBackButton = true,
            initialConfiguration = Config.Splash,
            childFactory = ::childFactory
        )

    private val _isDarkTheme = MutableValue(false)
    override val isDarkTheme: Value<Boolean> = _isDarkTheme


    private val downloadModel = DefaultDownloadModel(this, downloader, parentVideoDir)

    init {
        downloadModel
    }

    private fun childFactory(
        config: Config,
        context: ComponentContext
    ): Child {
        return when (config) {
            is Config.Splash -> Child.Splash(
                DefaultSplashComponent(context, object : SplashComponentCallback {
                    override fun onSplashFinish() {
                        // Open the Browser with default homepage
                        navigation.replaceCurrent(Config.Browser(TabPage.Homepage))
                    }
                })
            )

            is Config.Browser -> Child.Browser(
                DefaultBrowserComponent(
                    context = context,
                    initialPage = config.initialPage,
                    isDarkTheme = _isDarkTheme,
                    callback = object : BrowserComponentCallback {
                        override fun onThemeToggle() {
                            with(_isDarkTheme) { value = value.not() }
                        }

                        override fun onDownloadVideo(
                            videoUrl: String,
                            onDownloadAdd: () -> Unit,
                            onFailed: () -> Unit
                        ) {
                            downloadModel.onEvent(
                                DownloadModel.Event.AddDownload(
                                    videoUrl,
                                    onDownloadAdd,
                                    onFailed
                                )
                            )
                        }
                    }
                )
            )

            is Config.Downloads -> Child.Downloads(
                DefaultDownloadsComponent(
                    context = context,
                    downloads = downloadModel.downloads,
                    callback = object : DownloadComponentCallback {}
                )
            )
        }
    }

    override fun onEvent(e: Event) {
        when (e) {
            Event.OnDownloadMenuSelect -> {
                if (childStack.value.active.configuration !is Config.Downloads)
                    navigation.push(Config.Downloads)
            }

            Event.OnTabMenuSelect -> {
                if (childStack.value.active.configuration is Config.Downloads)
                    navigation.pop()
            }
        }
    }
}