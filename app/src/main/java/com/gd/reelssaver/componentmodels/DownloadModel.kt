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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

interface DownloadModel : Events<DownloadModel.Event> {
    val downloads: Value<List<Download>>

    sealed interface Event {
        data class AddDownload(
            val url: String,
            val onAddDownload: () -> Unit,
            val onFailed: () -> Unit
        ) : Event
    }
}

class DefaultDownloadModel(
    componentContext: ComponentContext,
    private val downloader: Downloader,
    private val parentDir: File
) : DownloadModel,
    ComponentScopeOwner by DefaultComponentScopeOwner(componentContext) {
    companion object {
        val TAG = DefaultDownloadModel::class.simpleName
    }

    private val _downloads = MutableValue(emptyList<Download>())
    override val downloads: Value<List<Download>> = _downloads

    private val _isLoading = MutableValue(true)

    init {
        scope.launch {
            _downloads.value = downloader.getAll()
            _isLoading.value = false
        }
    }

    override fun onEvent(e: DownloadModel.Event) {
        when (e) {
            is DownloadModel.Event.AddDownload -> download(e)
        }
    }


    private fun download(input: DownloadModel.Event.AddDownload) {
        scope.launch(Dispatchers.IO) {
            val flow = downloader.addDownload(input.url, parentDir)

            flow.collect { event: DownloadEvent ->
                when (event) {
                    is DownloadEvent.OnAddNew -> with(_downloads) {
                        Log.d(TAG, "Added new download ${event.download}")
                        value = value + event.download

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
                    // todo: Handle this case
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