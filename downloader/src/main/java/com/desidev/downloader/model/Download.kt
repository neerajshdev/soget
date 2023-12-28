package com.desidev.downloader.model

import io.ktor.http.ContentType
import java.time.LocalDate

data class Download(
    val id: Long,
    val name: String,
    val url: String,
    val localPath: String,
    val contentSize: Long,
    val downloaded: Long,
    val type: ContentType,
    val status: Status,
    val time: LocalDate = LocalDate.now()
) {
    enum class Status {
        InProgress, Complete
    }
}