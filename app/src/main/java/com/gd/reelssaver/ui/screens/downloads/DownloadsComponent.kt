package com.gd.reelssaver.ui.screens.downloads

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.callbacks.OnDownloadVideo
import com.gd.reelssaver.util.Events

interface DownloadsComponent : Events<DownloadsComponent.Event> {
    val downloads: Value<List<Download>>

    sealed interface Event {
    }


}

interface DownloadComponentCallback


class DefaultDownloadsComponent(
    context: ComponentContext,
    override val downloads: Value<List<Download>>,
    private val callback: DownloadComponentCallback
) : DownloadsComponent,
    ComponentContext by context {

    override fun onEvent(e: DownloadsComponent.Event) {
    }
}