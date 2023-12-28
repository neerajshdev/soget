package com.desidev.downloader

import com.desidev.downloader.database.getDownloads
import com.desidev.downloader.database.putDownload
import com.desidev.downloader.model.Download
import com.desidev.downloader.database.ObjectBox
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.util.moveToByteArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
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
            BrowserUserAgent()
            engine {
                requestTimeout = 15000
            }
        }
    }

    override suspend fun addDownload(
        url: String,
        parentDir: File,
        name: String?
    ): Result<Flow<DownloadEvent>, Error> {
        val filename = name ?: URL(url).path.substringAfterLast("/")
        val localFile = createNewFile(parentDir, filename)

        val response = client.get(url)
        if (response.status != HttpStatusCode.OK) return Result.Err(Error.ServerDisAllowed(response.status.value))

        val stream = try {
            localFile.outputStream()
        } catch (ex: IOException) {
            return Result.Err(Error.FailedWithIoException(ex))
        }

        val contentSize = response.contentLength() ?: 1
        val contentType = response.contentType() ?: ContentType.Any
        val flow = channelFlow {
            val channel = response.bodyAsChannel()
            var state = Download(
                id = 0,
                name = filename,
                localPath = localFile.path,
                url = url,
                contentSize = contentSize,
                downloaded = 0L,
                type = contentType,
                status = Download.Status.InProgress
            )

            send(DownloadEvent.OnAddNew(state))

            while (!channel.isClosedForRead) {
                channel.read(1024) { buffer -> stream.write(buffer.moveToByteArray()) }

                state = state.copy(
                    downloaded = channel.totalBytesRead,
                    status = if (contentSize == channel.totalBytesRead) Download.Status.Complete else Download.Status.InProgress
                )

                launch { send(DownloadEvent.OnProgress(state)) }
            }

            // Download complete
            putDownload(state)
        }

        return Result.Ok(flow)
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