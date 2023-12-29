package com.desidev.downloader.model

import io.ktor.http.ContentType
import java.time.LocalDate

data class Download(
    val id: Long,
    val status: Status,
    val type: ContentType,
    val name: String,
    val contentSize: Long,
    val downloaded: Long,
    val localPath: String,
    val url: String,
    val time: LocalDate = LocalDate.now()
) {
    enum class Status {
        InProgress, Complete
    }
}