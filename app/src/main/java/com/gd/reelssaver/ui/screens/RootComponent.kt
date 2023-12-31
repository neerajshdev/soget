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
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnPause
import com.arkivanov.essenty.lifecycle.doOnResume
import com.desidev.downloader.Downloader
import com.desidev.downloader.model.Download
import com.gd.reelssaver.componentmodels.DefaultDownloadModel
import com.gd.reelssaver.componentmodels.DownloadModel
import com.gd.reelssaver.ui.callbacks.OnExitConfirm
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
    val openExitDialog: Value<Boolean>
}

sealed interface Event {
    object OnTabMenuSelect : Event
    object OnDownloadMenuSelect : Event
    object OnExitDialogDismiss : Event
    object OnExitConfirm : Event
}

interface RootComponentCallback : DownloadComponentCallback, OnExitConfirm {
    override fun onRemoveDownload(download: List<Download>) {
    }
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
    appname: String,
    val initialPage: TabPage = TabPage.Homepage,
    val callback: RootComponentCallback
) : RootComponent, ComponentContext by componentContext {

    private val _openExitDialog = MutableValue(false)
    override val openExitDialog: Value<Boolean> = _openExitDialog

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<Config, Child>> =
        childStack(
            source = navigation,
            handleBackButton = false,
            initialConfiguration = Config.Splash,
            childFactory = ::childFactory
        )

    init {
        val backCallback = BackCallback {
            if (childStack.value.items.size > 1) {
                navigation.pop()
                println("pop current config")
            } else {
                _openExitDialog.value = true
            }
        }
        doOnResume {
            backHandler.register(backCallback)
        }

        doOnPause {
            backHandler.unregister(backCallback)
        }
    }

    private val _isDarkTheme = MutableValue(false)
    override val isDarkTheme: Value<Boolean> = _isDarkTheme


    private val downloadModel = DefaultDownloadModel(this, downloader, parentVideoDir, appname)

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
                        navigation.replaceCurrent(Config.Browser(initialPage))
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
                    callback = object : DownloadComponentCallback by callback {
                        override fun onRemoveDownload(download: List<Download>) {
                            downloadModel.onEvent(DownloadModel.Event.RemoveDownload(download))
                        }
                    }
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

            Event.OnExitDialogDismiss -> _openExitDialog.value = false
            Event.OnExitConfirm -> callback.onExitConfirm()
        }
    }
}