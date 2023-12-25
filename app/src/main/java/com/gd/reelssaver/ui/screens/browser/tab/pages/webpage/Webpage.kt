package com.gd.reelssaver.ui.screens.browser.tab.pages.webpage

import android.webkit.WebView
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.gd.reelssaver.model.VideoData
import com.gd.reelssaver.util.Events
import com.gd.reelssaver.util.Option


sealed interface Event {
    data class OnPageLoaded(val pageUrl: String) : Event
    class OnWebViewCreated(val webView: WebView) : Event
    class DownloadVideo(val videoData: VideoData, val appname: String) : Event
    data object OpenTabChooser : Event
    data object OpenSearchedVideo : Event
    data object OnGoBack : Event
    data object ToggleTheme : Event
    data class LoadUrl(val url: String) : Event
}

interface Webpage : Events<Event> {
    class Model(
        val view: Option<WebView>,
        val pageTitle: String,
        val pageUrl: String
    )

    val model: Value<Model>
    val searchedVideos: Value<List<VideoData>>
    val tabsCount: Value<Int>
    val isDarkTheme: Value<Boolean>
}


class DefaultWebpage(
    context: ComponentContext,
    override val isDarkTheme: Value<Boolean>,
    override val tabsCount: Value<Int>
) : Webpage, ComponentContext by context {

    private val _searchedVideos = MutableValue(listOf<VideoData>())
    override val searchedVideos: Value<List<VideoData>> = _searchedVideos

    private val _model =
        MutableValue(
            Webpage.Model(
                view = Option.None,
                pageTitle = "Unknown",
                "Unknown"
            )
        )

    override val model: Value<Webpage.Model> = _model
    override fun onEvent(e: Event) {
    }
}