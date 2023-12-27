package com.desidev.downloader

import com.desidev.downloader.model.Download
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

class DefaultDownloader : Downloader {

    val client by lazy {
        HttpClient(CIO) {
            BrowserUserAgent()
            engine {
                requestTimeout = 15000
            }
        }
    }

    override suspend fun addDownload(
        url: String,
        dir: String,
        name: String?
    ): Result<Flow<DownloadEvent>, Error> {
        val filename = name ?: URL(url).path.substringAfterLast("/")
        val response = client.get(url)
//        val subtype = response.contentType()?.contentSubtype ?: "bin"
        if (response.status != HttpStatusCode.OK) return Result.Err(Error.ServerDisAllowed(response.status.value))

        val stream = try {
            File(dir, filename).outputStream()
        } catch (ex: IOException) {
            return Result.Err(Error.FailedWithIoException(ex))
        }

        val contentSize = response.contentLength() ?: 1
        val contentType = response.contentType() ?: ContentType.Any
        val flow = channelFlow {
            val channel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                channel.read(1024) { buffer ->
                    stream.write(buffer.moveToByteArray())
                    launch {
                        send(
                            DownloadEvent.DownloadUpdate(
                                Download(
                                    0,
                                    name = filename,
                                    url = url,
                                    contentSize = contentSize,
                                    downloaded = channel.totalBytesRead,
                                    type = contentType
                                )
                            )
                        )
                    }
                }
            }
        }

        return Result.Ok(flow)
    }

    override fun cancelDownload(id: Long): Boolean {
        return false
    }
}