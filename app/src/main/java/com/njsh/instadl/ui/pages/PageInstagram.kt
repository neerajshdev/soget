package com.njsh.instadl.ui.pages

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.njsh.instadl.Application
import com.njsh.instadl.Executor
import com.njsh.instadl.FirebaseKeys
import com.njsh.instadl.instagram.EntityInstaReel
import com.njsh.instadl.instagram.FetchInstaReel
import com.njsh.instadl.navigation.Page
import com.njsh.instadl.ui.components.InputPasteAndGet

class PageInstagram : Page("Instagram")
{
    private val inputUrl = InputPasteAndGet()
    private val instaReel: MutableState<EntityInstaReel?> = mutableStateOf(null)

    init
    {
        inputUrl.eventOnGetClick = {
            Executor.execute {
                val fetchReel = FetchInstaReel(inputUrl.text.value).apply {
                    ds_user_id = Firebase.remoteConfig.getString(FirebaseKeys.DS_USER_ID)
                    sessionId = Firebase.remoteConfig.getString(FirebaseKeys.SESSION_ID)
                }

                fetchReel.eHandleInvalidInput = { Application.toast("Invalid url") }
                try
                {
                    instaReel.value = fetchReel.invoke()
                } catch (ex: Exception)
                {
                    ex.printStackTrace()
                }
            }
        }

        inputUrl.eventOnPasteClick = {
            inputUrl.text.value = Application.clipBoardData()
        }

        val parentModifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        val inputUrlModifier = Modifier.fillMaxWidth()

        addContent {
            Column(modifier = parentModifier) {
                inputUrl.Compose(inputUrlModifier)
                if (instaReel.value != null)
                {
                    val reel = instaReel.value!!
                    Reel(reel = reel, modifier = Modifier.weight(1f))
                    Button(
                        onClick = this@PageInstagram::download, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "DOWNLOAD")
                    }
                }
            }
        }
    }

    @Composable
    private fun Reel(reel: EntityInstaReel, modifier: Modifier = Modifier)
    {
        AsyncImage(
            model = reel.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxWidth()
        )
    }


    /**
     * Download instagram reel with download manager
     * @return returns the download reference
     */
    private fun download(): Long
    {
        val reel = instaReel.value!!
        val downloadManager = Application.getAppContext()
            .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val req = DownloadManager.Request(Uri.parse(instaReel.value!!.url))
        req.apply {
            setTitle(reel.title)
            setDescription("Instagram reel")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, reel.title)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setMimeType("video/${reel.ext}")
        }

        Application.toast("Download Started!")
        return downloadManager.enqueue(req)
    }
}