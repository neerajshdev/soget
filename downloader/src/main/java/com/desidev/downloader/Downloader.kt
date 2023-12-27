package com.desidev.downloader

import com.desidev.downloader.model.Download
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking

interface Downloader {
    suspend fun addDownload(
        url: String,
        dir: String,
        name: String? = null,
    ) : Result<Flow<DownloadEvent>, Error>
    fun cancelDownload(id: Long): Boolean
}