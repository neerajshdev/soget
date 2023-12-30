package com.desidev.downloader.database

import com.desidev.downloader.model.Download
import io.ktor.http.ContentType
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.kotlin.awaitCallInTx
import java.time.LocalDate

@Entity
internal data class DownloadEntity(
    @Id var id: Long,
    var name: String,
    val url: String,
    var localPath: String,
    var type: String,
    var contentSize: Long,
    var downloadedSize: Long,
    var status: String,
    var time: String
)





