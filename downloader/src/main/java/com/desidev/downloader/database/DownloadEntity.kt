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
        id,
        name,
        localPath,
        url,
        type.toString(),
        contentSize,
        downloaded,
        status.name,
        time.toString()
    )
}


private fun DownloadEntity.toModel() = run {
    Download(
        id,
        name,
        localPath,
        url,
        contentSize,
        downloadedSize,
        ContentType.parse(type),
        Download.Status.valueOf(status),
        LocalDate.parse(time)
    )
}