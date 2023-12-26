package com.gd.reelssaver.ui.screens.browser.tab.pages.webpage

import android.webkit.WebView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.gd.reelssaver.model.VideoData
import com.gd.reelssaver.ui.composables.searchVideoElement
import com.gd.reelssaver.ui.util.componentScope
import com.gd.reelssaver.util.Events
import com.gd.reelssaver.util.Option
import com.gd.reelssaver.util.asSome
import com.gd.reelssaver.util.isSome
import kotlinx.coroutines.launch


interface WebpageComponent : Events<Event> {
    data class Model(
        val view: Option<WebView>,
        val pageTitle: String,
        val pageUrl: String
    )

    val model: Value<Model>
    val searchedVideos: Value<List<VideoData>>
    val tabsCount: Value<Int>
    val isDarkTheme: Value<Boolean>

    val showSearchedVideos: Value<Boolean>
}


class DefaultWebpageComponent(
    context: ComponentContext,
    siteUrl: String,
    override val isDarkTheme: Value<Boolean>,
    override val tabsCount: Value<Int>,
    val callback: WebpageComponentCallback
) : WebpageComponent, ComponentContext by context {

    private val _searchedVideos = MutableValue(listOf<VideoData>())
    override val searchedVideos: Value<List<VideoData>> = _searchedVideos

    private val _showSearchedVideos = MutableValue(false)
    override val showSearchedVideos = _showSearchedVideos

    private val _model =
        MutableValue(
            WebpageComponent.Model(
                view = Option.None,
                pageTitle = "Unknown",
                pageUrl = siteUrl
            )
        )

    override val model: Value<WebpageComponent.Model> = _model

    val scope = componentScope()
    override fun onEvent(e: Event) {
        when (e) {
            is Event.OnPageLoaded -> with(_model) {
                value = value.copy(pageUrl = e.pageUrl, pageTitle = e.pageTitle)
            }

            is Event.DownloadVideo -> {}
            is Event.LoadUrl -> {}
            is Event.OnGoBack -> {}
            is Event.OnWebViewCreated -> {}
            is Event.OpenTabChooser -> callback.openTabChooser()
            is Event.ToggleTheme -> callback.toggleTheme()
            is Event.OnSearchVideoDismiss -> _showSearchedVideos.value = false
            is Event.OpenSearchedVideo -> {
                with(model.value.view) {
                    if (isSome()) {
                        scope.launch {
                            _showSearchedVideos.value = true
                            _searchedVideos.value = searchVideoElement(asSome().value)
                        }
                    }
                }
            }
        }
    }
}


interface WebpageComponentCallback {
    fun openTabChooser()
    fun toggleTheme()
}

sealed interface Event {
    data class OnPageLoaded(val pageUrl: String, val pageTitle: String) : Event
    class OnWebViewCreated(val webView: WebView) : Event
    class DownloadVideo(val videoData: VideoData, val appname: String) : Event
    data object OpenTabChooser : Event
    data object OpenSearchedVideo : Event
    data object OnGoBack : Event
    data object ToggleTheme : Event
    object OnSearchVideoDismiss : Event

    data class LoadUrl(val url: String) : Event
}