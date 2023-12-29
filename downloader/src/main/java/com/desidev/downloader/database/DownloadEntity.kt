package com.desidev.downloader.database

import com.desidev.downloader.model.Download
import io.ktor.http.ContentType
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.kotlin.awaitCallInTx
import java.time.LocalDate

@Entity
internal data class DownloadEntity(
    @Id var id: Long,
    var name: String,
    val url: String,
    var localPath: String,
    var type: String,
    var contentSize: Long,
    var downloadedSize: Long,
    var status: String,
    var time: String
)


suspend fun putDownload(download: Download) {
    ObjectBox.store.awaitCallInTx {
        ObjectBox.store.boxFor(DownloadEntity::class.java).put(download.toEntity())
    }
}

suspend fun getDownloads() = ObjectBox.store.awaitCallInTx {
    ObjectBox.store.boxFor(DownloadEntity::class.java).all.map { it.toModel() }
}


private fun Download.toEntity() = run {
    DownloadEntity(
        id = id,
        name = name,
        localPath = localPath,
        url = url,
        type = type.toString(),
        contentSize = contentSize,
        downloadedSize = downloaded,
        status = status.name,
        time = time.toString()
    )
}


private fun DownloadEntity.toModel() = run {
    Download(
        id = id,
        name = name,
        localPath = localPath,
        url = url,
        contentSize = contentSize,
        downloaded = downloadedSize,
        type = ContentType.parse(type),
        status = Download.Status.valueOf(status),
        time = LocalDate.parse(time)
    )
}