package com.desidev.downloader

import com.desidev.downloader.database.ObjectBox
import com.desidev.downloader.database.getDownloads
import com.desidev.downloader.database.putDownload
import com.desidev.downloader.model.Download
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.CurlUserAgent
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.printStack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import java.net.URL


internal var downloaderInstance: Downloader? = null

fun Downloader(dbDir: File) =
    downloaderInstance ?: DefaultDownloader(dbDir).also { downloaderInstance = it }

class DefaultDownloader internal constructor(private val dbDir: File) : Downloader {
    init {
        require(dbDir.isDirectory) { "parameter dbDir is required to be directory" }
        require(dbDir.exists()) { "dbDir does not exists" }
        require(dbDir.canRead() && dbDir.canWrite()) { "dbDir does not have read/write permissions" }
        ObjectBox.init(dbDir)
    }

    private val client by lazy {
        HttpClient(CIO) {
            CurlUserAgent()
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }
        }
    }

    override suspend fun addDownload(
        url: String,
        parentDir: File,
        name: String?
    ): Flow<DownloadEvent> {
        val filename = name ?: URL(url).path.substringAfterLast("/")
        val file = createNewFile(parentDir, filename)

        return channelFlow {
            client.prepareGet(url).execute { response: HttpResponse ->
                var download = Download(
                    id = 0,
                    name = filename,
                    localPath = file.path,
                    url = url,
                    contentSize = response.contentLength() ?: -1,
                    downloaded = 0L,
                    type = response.contentType() ?: ContentType.Any,
                    status = Download.Status.InProgress
                )

                send(DownloadEvent.OnAddNew(download))

                try {
                    val channel = response.bodyAsChannel()
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.isEmpty) {
                            val bytes = packet.readBytes()
                            file.appendBytes(bytes)
                            download =
                                download.copy(downloaded = download.downloaded + bytes.size)
                            send(DownloadEvent.OnProgress(download))
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStack()
                }

                download = download.copy(status = Download.Status.Complete)
                putDownload(download)
                send(DownloadEvent.OnComplete(download))
            }
        }
    }

    override suspend fun getAll(): List<Download> = getDownloads() ?: emptyList()

    override fun cancelDownload(id: Long): Boolean {
        return false
    }

    private fun createNewFile(parentDir: File, fileName: String): File {
        // Create a file object with the given file name
        var file = File(parentDir, fileName)
        // Initialize a counter for the file name suffix
        var counter = 0
        // Loop until the file does not exist
        while (file.exists()) {
            // Increment the counter
            counter++
            // Append the counter to the file name before the extension
            val newName =
                fileName.substringBeforeLast(".") + " ($counter)." + fileName.substringAfterLast(".")
            // Create a new file object with the new name
            file = File(parentDir, newName)
        }
        // Create the new file
        file.createNewFile()
        return file
    }
}