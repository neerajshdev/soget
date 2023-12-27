package com.desidev.downloader.database

import com.desidev.downloader.model.Download
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
internal data class DownloadEntity(
    @Id var id: Long,
    var name: String,
    val url: String,
    var type: String,
    var contentSize: Long,
    var downloadedSize: Long,
    var downloadTime: Long
)

internal object EntityStatus {
    val downloadComplete = Download.Status.Complete.name
    val inProgress = Download.Status.InProgress.name
}