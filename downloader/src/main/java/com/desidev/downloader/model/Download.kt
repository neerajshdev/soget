package com.desidev.downloader.model

import io.ktor.http.ContentType
import java.time.LocalDateTime

data class Download(
    val id: Long,
    val status: Status,
    val time: LocalDateTime = LocalDateTime.now(),
    val type: ContentType,
    val name: String,
    val contentSize: Long,
    val downloaded: Long,
    val localPath: String,
    val url: String
) {
    enum class Status {
        InProgress, Complete
    }
}