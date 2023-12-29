package com.gd.reelssaver.ui.callbacks

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
