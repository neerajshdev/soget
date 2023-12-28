package com.gd.reelssaver.ui.screens.downloads

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.desidev.downloader.model.Download
import com.gd.reelssaver.componentmodels.DownloadModel
import com.gd.reelssaver.util.Events

interface DownloadsComponent : Events<DownloadsComponent.Event> {
    val downloads: Value<List<Download>>

    sealed interface Event {
        data class AddDownload(val url: String) : Event
    }

    interface Callback {
        fun addDownload(url: String)
    }
}

class DefaultDownloadsComponent(
    context: ComponentContext,
    private val downloadModel: DownloadModel,
    private val callback: DownloadsComponent.Callback
) : DownloadsComponent,
    ComponentContext by context {

    override val downloads: Value<List<Download>> = downloadModel.downloads

    override fun onEvent(e: DownloadsComponent.Event) {
        when (e) {
            is DownloadsComponent.Event.AddDownload -> callback.addDownload(e.url)
        }
    }
}