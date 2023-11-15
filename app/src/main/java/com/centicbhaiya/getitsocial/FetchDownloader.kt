package com.centicbhaiya.getitsocial

import android.content.Context
import android.util.Log
import com.centicbhaiya.getitsocial.ui.state.DownloadState
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2core.DownloadBlock
import online.desidev.onestate.OneState
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FetchDownloader(context: Context, val downloadState: OneState<DownloadState>) :
    FetchListener {
    companion object {
        val TAG = FetchDownloader::class.simpleName
    }

    private val fetch: Fetch

    init {
        val fetchConfig = FetchConfiguration.Builder(context).setDownloadConcurrentLimit(3).build()
        fetch = Fetch.getInstance(fetchConfig).addListener(this)
        fetch.getDownloads { downloads ->
            downloadState.send {
                it.apply {
                    addDownload(downloads)
                }
            }
        }
    }

    fun downloadFile(url: String, path: String) {
        val request = Request(url, path)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL
        request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG")

        Log.d(TAG, "download request: $request")

        fetch.enqueue(request,
            { updatedRequest ->
                Log.d(TAG, "downloadFile: download request: $updatedRequest")
            }, { error ->
                Log.d(TAG, "error: $error")
            }
        )
    }

    override fun onAdded(download: Download) {
        downloadState.send {
            it.apply { addDownload(download) }
        }
    }

    override fun onCancelled(download: Download) {
        downloadState.send {
            it.apply {
                update(download)
            }
        }
    }

    override fun onCompleted(download: Download) {
        downloadState.send { it.apply { update(download) } }
    }

    override fun onDeleted(download: Download) {
        downloadState.send { it.apply { removeDownload(download) } }
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
        Log.d(TAG, "onDownloadBlockUpdated: ")
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        Log.d(TAG, "onError: $error")
    }

    override fun onPaused(download: Download) {
        downloadState.send { it.apply { update(download) } }
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        Log.d(TAG, "onProgress: $download /$downloadedBytesPerSecond")
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        Log.d(TAG, "onQueued: $download")
    }

    override fun onRemoved(download: Download) {
        downloadState.send { it.apply { removeDownload(download) } }
    }

    override fun onResumed(download: Download) {
        downloadState.send { it.apply { update(download) } }
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        Log.d(TAG, "onStarted: $download")
    }

    override fun onWaitingNetwork(download: Download) {
        Log.d(TAG, "onWaitingNetwork: $download")
    }
}