package com.desidev.downloader

import com.desidev.downloader.model.Download

sealed interface DownloadEvent {
    data class OnAddNew(val download: Download): DownloadEvent
    data class OnProgress(val download: Download): DownloadEvent
    data class OnComplete(val download: Download): DownloadEvent
    class OnCancelled(val download: Download): DownloadEvent
    class OnError(s: String) : DownloadEvent
}