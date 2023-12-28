package com.gd.reelssaver.componentmodels

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.desidev.downloader.DownloadEvent
import com.desidev.downloader.Downloader
import com.desidev.downloader.Error
import com.desidev.downloader.Result
import com.desidev.downloader.model.Download
import com.gd.reelssaver.ui.util.ComponentScopeOwner
import com.gd.reelssaver.ui.util.DefaultComponentScopeOwner
import com.gd.reelssaver.util.Events
import kotlinx.coroutines.launch
import java.io.File

interface DownloadModel : Events<DownloadModel.Event> {
    val downloads: Value<List<Download>>

    sealed interface Event {
        data class AddDownload(val url: String) : Event
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
            is DownloadModel.Event.AddDownload -> download(e.url)
        }
    }


    private fun download(url: String) {
        scope.launch {
            val flow = when (val result = downloader.addDownload(url, parentDir)) {
                is Result.Ok -> result.value
                is Result.Err -> {
                    when (val err = result.err) {
                        is Error.ServerDisAllowed -> {
                            Log.d(TAG, "Failed with status code! : ${err.statusCode}")
                        }

                        is Error.FailedWithIoException -> {
                            Log.d(TAG, "Failed with io exception : ${err.ex}")
                        }
                    }
                    return@launch
                }
            }

            flow.collect { event: DownloadEvent ->
                when (event) {
                    is DownloadEvent.OnAddNew -> with(_downloads) {
                        Log.d(TAG, "Added new download ${event.download}")
                        value = value + event.download
                    }
                    is DownloadEvent.OnProgress -> with(_downloads){
                        Log.d(TAG, "On download update: ${event.download}")
                        value = value.map { if (it.id == event.download.id) event.download else it  }
                    }
                    is DownloadEvent.OnCancelled -> Log.d(TAG, "Download was cancelled. ${event.download}")
                        // todo: Handle this case
                }
            }
        }
    }
}