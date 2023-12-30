package com.desidev.downloader.database

import com.desidev.downloader.model.Download
import io.ktor.http.ContentType
import io.objectbox.BoxStore
import io.objectbox.kotlin.awaitCallInTx
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

object ObjectBox {
    lateinit var store: BoxStore private set
    fun init(dir: File) {
        store = MyObjectBox.builder()
            .directory(dir)
            .build()
    }

    suspend fun putDownload(download: Download): Download {
        val entity = download.toEntity()
        store.awaitCallInTx {
            store.boxFor(DownloadEntity::class.java).put(entity)
        }
        return entity.toModel()
    }

    suspend fun getDownloads() = store.awaitCallInTx {
        store.boxFor(DownloadEntity::class.java).all.map { it.toModel() }
    }

    suspend fun removeDownloads(downloads: List<Download>) {
        store.awaitCallInTx {
            store.boxFor(DownloadEntity::class.java).remove(downloads.map { it.toEntity() })
        }
    }


    suspend fun removeDownload(download: Download) {
        store.awaitCallInTx {
            store.boxFor(DownloadEntity::class.java).remove(download.id)
        }
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
        time = LocalDateTime.parse(time)
    )
}