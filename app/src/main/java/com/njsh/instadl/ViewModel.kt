package com.njsh.instadl

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.njsh.instadl.entity.EntityInstaReel
import com.njsh.instadl.usecase.FetchInstaReel

object ViewModel
{
    val instagram = InstagramViewModel()
}


class InstagramViewModel
{
    val reelState: MutableState<EntityInstaReel?> = mutableStateOf(null)

    fun getContent(url: String, callbacks: (Result) -> Unit)
    {
        val fetchInstaReel = FetchInstaReel(url)

        fetchInstaReel.handleExcep = { exception ->
            callbacks.invoke(Result.Failed(exception.message ?: "Something went wrong"))
        }

        Executor.execute {
            fetchInstaReel.invoke()
        }
    }


    /**
     * Download instagram reel with download manager
     * @return returns the download reference
     */
    fun download()
    {
        val reel = reelState.value!!
        val downloadManager =
            App.instace().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val req = DownloadManager.Request(Uri.parse(reel.url))

        req.apply {
            setTitle(reel.title)
            setDescription("Instagram reel")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, reel.title)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setMimeType("video/${reel.ext}")
        }
        downloadManager.enqueue(req)
    }
}


class FacebookVideo
{
//    val video: MutableState<>
}

sealed class Result()
{
    object Success : Result()
    data class Failed(val msg: String) : Result()
}