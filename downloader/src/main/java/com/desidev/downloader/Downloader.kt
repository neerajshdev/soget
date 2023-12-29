package com.desidev.downloader

import com.desidev.downloader.model.Download
import kotlinx.coroutines.flow.Flow
import java.io.File

interface Downloader {
    suspend fun addDownload(
        url: String,
        parentDir: File,
        name: String? = null,
    ) : Flow<DownloadEvent>

    suspend fun getAll(): List<Download>
    fun cancelDownload(id: Long): Boolean
}