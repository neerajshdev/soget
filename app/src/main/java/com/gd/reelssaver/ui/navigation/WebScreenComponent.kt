package com.gd.reelssaver.ui.navigation

import android.webkit.WebView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.gd.reelssaver.model.VideoData
import com.gd.reelssaver.ui.components.searchVideoElement
import com.gd.reelssaver.ui.model.Tab
import com.gd.reelssaver.util.createFileName
import com.gd.reelssaver.util.download
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface WebScreenComponent {
    val tabs: Value<List<Tab>>
    val activeTab: Value<Tab>
    val views: Map<String, WebView>
    val activeView: StateFlow<WebView?>

    val videosOnPage: Value<List<VideoData>>

    fun onEvent(event: Event)
    sealed class Event {
        data class UpdateUrl(val url: String) : Event()
        class WebViewCreated(val webView: WebView) : Event()
        class DownloadVideo(val videoData: VideoData, val appname: String) : Event()
        data object OpenTabChooser : Event()
        data object GetVideosOnPage : Event()
    }
}


class DefaultWebScreenComponent(
    componentContext: ComponentContext,
    override val tabs: Value<List<Tab>>,
    override val activeTab: Value<Tab>,
    override val views: Map<String, WebView>,
    private val onTabUpdate: (old: Tab, new: Tab) -> Unit,
    private val onWebViewCreated: (key: String, view: WebView) -> Unit,
    private val onOpenTabChooser: () -> Unit
) : WebScreenComponent, ComponentContext by componentContext {
    private val _activeView = MutableStateFlow<WebView?>(null)
    override val activeView: StateFlow<WebView?> = _activeView
    private val scope = componentScope()

    private val _videosOnPage = MutableValue(emptyList<VideoData>())
    override val videosOnPage: Value<List<VideoData>> = _videosOnPage

    //    private val obj = instanceKeeper.getOrCreate("views") {    }
    override fun onEvent(event: WebScreenComponent.Event) {
        when (event) {
            is WebScreenComponent.Event.UpdateUrl -> {
                onTabUpdate(activeTab.value, activeTab.value.copy(url = event.url))
            }

            is WebScreenComponent.Event.WebViewCreated -> {
                onWebViewCreated(activeTab.value.id, event.webView)
            }

            is WebScreenComponent.Event.OpenTabChooser -> {
                onOpenTabChooser()
            }

            WebScreenComponent.Event.GetVideosOnPage -> {
                val view = views[activeTab.value.id]
                if (view != null) {
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
            }
        }
    }
}


class FakeWebScreenComponent() : WebScreenComponent {
    override val tabs: Value<List<Tab>> = MutableValue(emptyList())
    override val activeTab: Value<Tab> = MutableValue(Tab(""))
    override val views: Map<String, WebView> = HashMap()
    override val activeView: StateFlow<WebView?> = MutableStateFlow(null)
    override val videosOnPage: Value<List<VideoData>>
        get() = TODO("Not yet implemented")

    override fun onEvent(event: WebScreenComponent.Event) {
    }
}


private fun LifecycleOwner.componentScope(coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()): CoroutineScope {
    return CoroutineScope(coroutineContext).also {
        lifecycle.doOnDestroy { it.cancel() }
    }
}