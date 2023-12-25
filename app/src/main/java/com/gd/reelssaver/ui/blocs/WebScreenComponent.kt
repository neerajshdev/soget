package com.gd.reelssaver.ui.blocs

import android.webkit.WebView
import android.widget.Toast
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.gd.reelssaver.App
import com.gd.reelssaver.model.VideoData
import com.gd.reelssaver.ui.composables.searchVideoElement
import com.gd.reelssaver.util.createFileName
import com.gd.reelssaver.util.download
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL

interface WebScreenComponent {
    val currentUrl: StateFlow<String>
    val webView: StateFlow<WebView?>
    val videosOnPage: StateFlow<List<VideoData>>
    val isDarkTheme: StateFlow<Boolean>
    val pageCount: StateFlow<Int>

    fun onEvent(event: Event)
    sealed class Event {
        data class OnPageLoaded(val pageUrl: String) : Event()
        class OnWebViewCreated(val webView: WebView) : Event()
        class DownloadVideo(val videoData: VideoData, val appname: String) : Event()
        data object OpenTabChooser : Event()
        data object GetVideosOnPage : Event()
        data object OnGoBack : Event()
        data object ToggleTheme : Event()
        data class LoadUrl(val url: String) : Event()
    }
}


class DefaultWebScreenComponent(
    componentContext: ComponentContext,
    override val currentUrl: StateFlow<String>,
    override val isDarkTheme: StateFlow<Boolean>,
    override val pageCount: StateFlow<Int>,
    override val webView: StateFlow<WebView?>,
    onComponentDestroyed: () -> Unit,
    private val onViewCreated: (WebView) -> Unit,
    private val onPageLoaded: (url: String) -> Unit,
    private val onOpenTabChooser: () -> Unit,
    private val onGoBackToHome: () -> Unit,
    private val onToggleTheme: () -> Unit,
) : WebScreenComponent, ComponentContext by componentContext {
    private val scope = componentScope()

    private val _videosOnPage = MutableStateFlow(emptyList<VideoData>())
    override val videosOnPage: StateFlow<List<VideoData>> = _videosOnPage

    init {
        doOnDestroy(onComponentDestroyed)
    }

    override fun onEvent(event: WebScreenComponent.Event) {
        when (event) {
            is WebScreenComponent.Event.OnPageLoaded -> {
                onPageLoaded(event.pageUrl)
            }

            is WebScreenComponent.Event.OnWebViewCreated -> {
                onViewCreated(event.webView)
            }

            is WebScreenComponent.Event.OpenTabChooser -> {
                onOpenTabChooser()
            }

            WebScreenComponent.Event.GetVideosOnPage -> {
                webView.value?.let { view ->
                    scope.launch {
                        _videosOnPage.value = searchVideoElement(view)
                    }
                }
            }

            is WebScreenComponent.Event.DownloadVideo -> {
                download(
                    createFileName(event.appname),
                    event.videoData.videoUrl,
                    description = "Video downloaded by ${event.appname}"
                )
                App.toast("Video added to Download!", len = Toast.LENGTH_LONG)
            }

            WebScreenComponent.Event.OnGoBack -> {
                onGoBackToHome()
            }

            WebScreenComponent.Event.ToggleTheme -> {
                onToggleTheme()
            }

            is WebScreenComponent.Event.LoadUrl -> {
                try {
                    val url = URL(event.url)
                    webView.value?.loadUrl(url.toString())
                } catch (ex: MalformedURLException) {
                    ex.printStackTrace()
                    App.toast("Not a url", Toast.LENGTH_SHORT)
                }
            }
        }
    }
}


/*
class FakeWebScreenComponent(override val isDarkTheme: StateFlow<Boolean>) : WebScreenComponent {
    override val tabs: Value<List<Tab>> = MutableValue(emptyList())
    override val activeTab: StateFlow<Tab?> = MutableStateFlow(null)
    override val views: Map<String, WebView> = HashMap()
    override val activeView: StateFlow<WebView?> = MutableStateFlow(null)
    override val videosOnPage: Value<List<VideoData>>
        get() = TODO("Not yet implemented")

    override fun onEvent(event: WebScreenComponent.Event) {
    }
}*/
