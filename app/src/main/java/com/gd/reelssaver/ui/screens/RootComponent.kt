package com.gd.reelssaver.ui.screens

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
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
import com.gd.reelssaver.ui.screens.downloads.DownloadsComponent
import com.gd.reelssaver.ui.screens.splash.DefaultSplashComponent
import com.gd.reelssaver.ui.screens.splash.SplashComponent
import com.gd.reelssaver.ui.screens.splash.SplashComponentCallback
import kotlinx.parcelize.Parcelize
import java.io.File

interface RootComponent {
    val child: Value<ChildStack<Config, Child>>
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
    parentDir: File,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val child: Value<ChildStack<Config, Child>> =
        childStack(
            source = navigation,
            initialConfiguration = Config.Splash,
            childFactory = ::childFactory
        )

    private val _isDarkTheme = MutableValue(false)

    private val downloadModel = DefaultDownloadModel(this, downloader, parentDir)

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
                        override fun toggleTheme() {
                            with(_isDarkTheme) { value = value.not() }
                        }

                        override fun addDownload(url: String) {
                            // todo: start downloading from url
                        }
                    }
                )
            )

            is Config.Downloads -> Child.Downloads(
                DefaultDownloadsComponent(
                    context = context,
                    downloadModel = downloadModel,
                    callback = object : DownloadsComponent.Callback {
                        override fun addDownload(url: String) {
                            // delegate event to download model
                            downloadModel.onEvent(DownloadModel.Event.AddDownload(url))
                        }
                    }
                )
            )
        }
    }
}