package com.njsh.reelssaver.api

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.njsh.reelssaver.App

class DownloadableImpl(val url: String, val title: String, val type: String) : Downloadable
{
    override fun download()
    {
        val downloadManager =
            App.instance().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val req = DownloadManager.Request(Uri.parse(url))

        req.apply {
            setTitle(title)
            setDescription("Instagram url")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setMimeType(type)
        }
        downloadManager.enqueue(req)
    }
}