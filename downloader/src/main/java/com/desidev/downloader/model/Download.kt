package com.desidev.downloader.model

import io.ktor.http.ContentType

data class Download(
    val id: Long,
    val name: String,
    val url: String,
    val contentSize: Long,
    val downloaded: Long,
    val type: ContentType
) {
    enum class Status {
        InProgress, Complete
    }
}