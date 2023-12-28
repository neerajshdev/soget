package com.gd.reelssaver.ui.screens.downloads

import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.desidev.downloader.model.Download
import com.gd.reelssaver.util.Events
import io.ktor.http.ContentType
import java.time.LocalDate

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
    override val downloads: Value<List<Download>>,
    private val callback: DownloadsComponent.Callback
) : DownloadsComponent,
    ComponentContext by context {

    override fun onEvent(e: DownloadsComponent.Event) {
        when (e) {
            is DownloadsComponent.Event.AddDownload -> callback.addDownload(e.url)
        }
    }
}

class FakeDownloadComponent : DownloadsComponent {

    val item = Download(
        1,
        "sample.mp4",
        "https://scontent.fdel24-1.fna.fbcdn.net/v/t42.1790-2/409326485_314328708142241_242782183691640083_n.mp4?_nc_cat=110&ccb=1-7&_nc_sid=55d0d3&efg=eyJybHIiOjc3NywicmxhIjo4MzcsInZlbmNvZGVfdGFnIjoic3ZlX3NkIn0%3D&_nc_ohc=evseQJsxy20AX-ciKuL&_nc_rml=0&_nc_ht=scontent.fdel24-1.fna&oh=00_AfC4MPGodNlboNAGFCt-WPHAdIkwq5-ajriNfVTD5uT0qw&oe=6591C011",
        "",
        5000L,
        3000L,
        status = Download.Status.InProgress,
        type = ContentType.Video.MP4,
        time = LocalDate.now()
    )


    override val downloads: Value<List<Download>> = MutableValue(
        listOf(
            item, item.copy(id = 1, downloaded = 400), item.copy(status = Download.Status.Complete)
        )
    )

    override fun onEvent(e: DownloadsComponent.Event) {
    }
}