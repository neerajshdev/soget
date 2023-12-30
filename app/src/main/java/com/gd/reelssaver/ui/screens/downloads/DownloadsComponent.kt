package com.gd.reelssaver.ui.screens.downloads

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.callbacks.OnOpenVideoInPlayer
import com.gd.reelssaver.ui.callbacks.OnRemoveDownload
import com.gd.reelssaver.util.Events
import io.ktor.http.ContentType
import java.time.LocalDate

interface DownloadsComponent : Events<Event> {
    val downloads: Value<List<Download>>
}
sealed interface Event {
    // User can click on Downloaded Video item
    data class OnClickDownloadedItem(val item: Download): Event

    // User can remove a download item
    data class OnRemovedDownloadItem(val item: List<Download>): Event
}

interface DownloadComponentCallback : OnRemoveDownload, OnOpenVideoInPlayer


class DefaultDownloadsComponent(
    context: ComponentContext,
    override val downloads: Value<List<Download>>,
    private val callback: DownloadComponentCallback
) : DownloadsComponent,
    ComponentContext by context {

    override fun onEvent(e: Event) {
        when (e) {
            is Event.OnClickDownloadedItem -> callback.onOpenVideoInPlayer(e.item.localPath)
            is Event.OnRemovedDownloadItem -> callback.onRemoveDownload(e.item)
        }
    }
}

class FakeDownloadComponent: DownloadsComponent {
    override val downloads: Value<List<Download>> = MutableValue(fakeDownloads)
    override fun onEvent(e: Event) {

    }
}

val fakeDownloads = listOf(
    Download(
        id = 1,
        status = Download.Status.InProgress,
        type = ContentType.Video.MP4,
        name = "Big Buck Bunny",
        contentSize = 1024 * 1024 * 100, // 100 MB
        downloaded = 1024 * 1024 * 10, // 10 MB
        localPath = "/downloads/BigBuckBunny.mp4",
        url = "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4",
        time = LocalDate.of(2023, 1, 1)
    ),
    Download(
        id = 2,
        status = Download.Status.Complete,
        type = ContentType.Video.MP4,
        name = "Elephant Dream",
        contentSize = 1024 * 1024 * 50, // 50 MB
        downloaded = 1024 * 1024 * 50, // 50 MB
        localPath = "/downloads/ElephantDream.mp4",
        url = "https://www.sample-videos.com/video123/mp4/720/elephants_dream_720p_1mb.mp4",
        time = LocalDate.of(2023, 1, 2)
    ),
    // Add more fake Download items as needed
)