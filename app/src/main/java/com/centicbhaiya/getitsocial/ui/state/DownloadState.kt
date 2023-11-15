package com.centicbhaiya.getitsocial.ui.state

import com.tonyodev.fetch2.Download

class DownloadState {
    private var downloads = mutableListOf<Download>()
    fun getDownloads(): List<Download> = downloads

    fun addDownload(download: Download) {
        downloads.add(download)
    }

    fun addDownload(downloads: List<Download> ) {
        this.downloads.addAll(downloads)
    }

    fun update(download: Download) {
        val index = downloads.indexOfFirst { it.id == download.id }
        downloads[index] = download
    }

    fun removeDownload(download: Download) {
        downloads.removeIf { it.id == download.id }
    }
}