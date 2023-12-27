package com.desidev.downloader

import com.desidev.downloader.model.Download

sealed interface DownloadEvent {
    class DownloadUpdate(val download: Download): DownloadEvent
    class DownloadCancelled(val download: Download): DownloadEvent
}