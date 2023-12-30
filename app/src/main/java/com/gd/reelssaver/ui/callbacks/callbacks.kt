package com.gd.reelssaver.ui.callbacks

import com.desidev.downloader.model.Download
import java.net.URL

interface OnDownloadVideo {
    fun onDownloadVideo(videoUrl: String, onDownloadAdd: () -> Unit, onFailed: () -> Unit)
}

interface OnThemeToggle {
    fun onThemeToggle()
}

interface OnTabChooserOpen {
    fun onTabChooserOpen()
}

interface OnOpenWebSite {
    fun onOpenWebsite(url: URL)
}

interface OnRemoveDownload {
    fun onRemoveDownload(download: List<Download>)
}

interface OnOpenVideoInPlayer {
    fun onOpenVideoInPlayer(filepath: String)
}
