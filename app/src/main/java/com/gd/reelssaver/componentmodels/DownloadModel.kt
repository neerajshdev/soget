package com.gd.reelssaver.componentmodels

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.desidev.downloader.DownloadEvent
import com.desidev.downloader.Downloader
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.util.ComponentScopeOwner
import com.gd.reelssaver.ui.util.DefaultComponentScopeOwner
import com.gd.reelssaver.util.Events
import com.gd.reelssaver.util.createFileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneOffset

interface DownloadModel : Events<DownloadModel.Event> {
    val downloads: Value<List<Download>>

    sealed interface Event {
        data class AddDownload(
            val url: String,
            val onAddDownload: () -> Unit,
            val onFailed: () -> Unit
        ) : Event

        data class RemoveDownload(
            val downloads: List<Download>
        ) : Event
    }
}

class DefaultDownloadModel(
    componentContext: ComponentContext,
    private val downloader: Downloader,
    private val parentDir: File,
    private val appname: String
) : DownloadModel,
    ComponentScopeOwner by DefaultComponentScopeOwner(componentContext) {
    companion object {
        val TAG = DefaultDownloadModel::class.simpleName
    }

    private val _downloads = MutableValue(emptyList<Download>())
    override val downloads: Value<List<Download>> = _downloads


    init {
        reloadDownloads()
    }

    override fun onEvent(e: DownloadModel.Event) {
        when (e) {
            is DownloadModel.Event.AddDownload -> download(e)
            is DownloadModel.Event.RemoveDownload -> {
                scope.launch {
                    try {
                        e.downloads.forEach { File(it.localPath).delete() }
                        downloader.removeDownload(e.downloads)
                    } catch (_: Exception) {
                    }

                    reloadDownloads()
                }
            }
        }
    }


    private fun reloadDownloads() {
        scope.launch {
            val downloads = downloader.getAll()
            val downloadsToRemove = downloads.filter {
                try {
                    it.status == Download.Status.Complete && File(it.localPath).exists().not()
                } catch (ex: Exception) {
                    false
                }
            }
            launch {
                downloader.removeDownload(downloadsToRemove)
            }

            val comp = compareByDescending<Download> { it.time.toEpochSecond(ZoneOffset.UTC) }
            _downloads.value = downloads.minus(downloadsToRemove.toSet()).sortedWith(comp)

            _downloads.value.forEach {
                println(it)
            }
        }
    }


    private fun download(input: DownloadModel.Event.AddDownload) {
        scope.launch(Dispatchers.IO) {
            val name = createFileName()
            val flow = downloader.addDownload(input.url, parentDir, name = "$appname $name")

            flow.collect { event: DownloadEvent ->
                when (event) {
                    is DownloadEvent.OnAddNew -> with(_downloads) {
                        Log.d(TAG, "Added new download ${event.download}")
                        value = buildList {
                            add(event.download)
                            addAll(value)
                        }

                        withContext(Dispatchers.Main) {
                            input.onAddDownload()
                        }
                    }

                    is DownloadEvent.OnProgress -> with(_downloads) {
                        Log.d(TAG, "On download update: ${event.download}")
                        value = value.map { if (it.id == event.download.id) event.download else it }
                    }

                    is DownloadEvent.OnCancelled -> Log.d(
                        TAG,
                        "Download was cancelled. ${event.download}"
                    )

                    is DownloadEvent.OnComplete -> with(_downloads) {
                        value = value.map { if (it.id == event.download.id) event.download else it }

                        Log.d(
                            TAG,
                            "Download is complete: ${event.download}"
                        )
                    }
                }
            }
        }
    }
}